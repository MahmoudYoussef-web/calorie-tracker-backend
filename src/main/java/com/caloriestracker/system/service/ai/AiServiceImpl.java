package com.caloriestracker.system.service.ai;

import com.caloriestracker.system.dto.response.ai.AiAnalyzeResponse;
import com.caloriestracker.system.entity.*;
import com.caloriestracker.system.enums.ImageStatus;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.repository.*;
import com.caloriestracker.system.service.ai.client.AiMultiResult;
import com.caloriestracker.system.service.ai.client.AiResult;
import com.caloriestracker.system.service.ai.provider.AiVisionProvider;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.caloriestracker.system.util.ByteArrayMultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.stream.Stream;
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final MealRepository mealRepo;
    private final MealItemRepository itemRepo;
    private final ImageRepository imageRepo;
    private final DailySummaryRepository summaryRepo;
    private final FoodRepository foodRepo;
    private final AiVisionProvider visionProvider;

    private static final String UPLOAD_DIR = "uploads/";


    private static final String PROCESSING_FOOD_NAME = "__PROCESSING__";


    @Override
    @Transactional
    public AiAnalyzeResponse analyze(Long mealId, MultipartFile file) {

        Meal meal = mealRepo.findById(mealId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meal not found")
                );

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image is required");
        }

        String fileName =
                System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path path = Paths.get(UPLOAD_DIR + fileName);

        try {
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Upload failed");
        }

        Image image = Image.builder()
                .path(path.toString())
                .status(ImageStatus.PROCESSING)
                .user(meal.getUser())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .build();

        imageRepo.save(image);

        Food placeholderFood = getOrCreatePlaceholderFood();

        // Create Placeholder MealItem
        MealItem item = MealItem.builder()
                .meal(meal)
                .image(image)
                .food(placeholderFood)
                .quantity(1.0)
                .caloriesAtTime(0.0)
                .proteinAtTime(0.0)
                .carbsAtTime(0.0)
                .fatAtTime(0.0)
                .confidence(0.0)
                .build();

        itemRepo.save(item);

        image.setMealItem(item);
        imageRepo.save(image);

        // Run AI Async
        processAsync(image.getId(), file);

        AiAnalyzeResponse response = new AiAnalyzeResponse();
        response.setImageId(image.getId());
        response.setMealItemId(item.getId());
        response.setStatus(ImageStatus.PROCESSING);

        return response;
    }


    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAsync(Long imageId, MultipartFile file) {

        Image image = imageRepo.findById(imageId).orElseThrow();
        MealItem placeholderItem = image.getMealItem();
        Meal meal = placeholderItem.getMeal();

        try {
            AiMultiResult multiResult = visionProvider.analyzeMulti(file);

            if (multiResult.getItems() == null || multiResult.getItems().isEmpty()) {
                throw new RuntimeException("AI returned no items");
            }

            boolean isFirst = true;
            for (AiResult result : multiResult.getItems()) {

                Food food = foodRepo.findByNameIgnoreCase(result.getName())
                        .orElseGet(() -> foodRepo.save(
                                Food.builder()
                                        .name(result.getName())
                                        .calories(result.getCalories())
                                        .protein(0.0).carbs(0.0).fat(0.0)
                                        .build()
                        ));

                if (isFirst) {

                    placeholderItem.setFood(food);
                    placeholderItem.setQuantity(result.getQuantity() > 0 ? result.getQuantity() : 1.0);
                    placeholderItem.setCaloriesAtTime(result.getCalories());
                    placeholderItem.setConfidence(result.getConfidence());
                    itemRepo.save(placeholderItem);
                    isFirst = false;
                } else {
                    MealItem newItem = MealItem.builder()
                            .meal(meal)
                            .image(null)
                            .food(food)
                            .quantity(result.getQuantity() > 0 ? result.getQuantity() : 1.0)
                            .caloriesAtTime(result.getCalories())
                            .proteinAtTime(0.0)
                            .carbsAtTime(0.0)
                            .fatAtTime(0.0)
                            .confidence(result.getConfidence())
                            .build();
                    itemRepo.save(newItem);
                }
            }

            updateSummary(meal);
            image.setStatus(ImageStatus.DONE);

        } catch (Exception e) {
            log.error("AI processing failed: {}", e.getMessage());
            image.setStatus(ImageStatus.FAILED);
        }

        imageRepo.save(image);
    }


    private Food getOrCreatePlaceholderFood() {
        return foodRepo.findByNameIgnoreCase(PROCESSING_FOOD_NAME)
                .orElseGet(() ->
                        foodRepo.save(
                                Food.builder()
                                        .name(PROCESSING_FOOD_NAME)
                                        .calories(0.0)
                                        .protein(0.0)
                                        .carbs(0.0)
                                        .fat(0.0)
                                        .build()
                        )
                );
    }


    @Override
    @Transactional
    public void retry(Long imageId) {

        Image image = imageRepo.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        if (image.getStatus() != ImageStatus.FAILED) {
            throw new BadRequestException("Not failed image");
        }

        image.setStatus(ImageStatus.PROCESSING);
        imageRepo.save(image);

        MultipartFile fileToRetry = null;
        try {
            Path savedPath = Paths.get(image.getPath());
            if (Files.exists(savedPath)) {
                byte[] bytes = Files.readAllBytes(savedPath);
                String filename = savedPath.getFileName().toString();
                String contentType = image.getMimeType() != null ? image.getMimeType() : "image/jpeg";
                fileToRetry = new ByteArrayMultipartFile(bytes, filename, contentType);
            }
        } catch (IOException e) {

        }

        processAsync(image.getId(), fileToRetry);
    }


    @Override
    @Transactional(readOnly = true)
    public ImageStatus getStatus(Long imageId) {

        Image image = imageRepo.findById(imageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Image not found")
                );

        return image.getStatus();
    }


    private void updateSummary(Meal meal) {

        LocalDate date = meal.getMealDate();
        Long userId = meal.getUser().getId();

        double consumed = mealRepo
                .findByUser_IdAndMealDate(userId, date)
                .stream()
                .flatMap(m ->
                        m.getItems() == null
                                ? Stream.empty()
                                : m.getItems().stream()
                )
                .mapToDouble(i ->
                        i.getCaloriesAtTime() == null
                                ? 0.0
                                : i.getCaloriesAtTime()
                )
                .sum();

        DailySummary summary = summaryRepo
                .findByUserIdAndDate(userId, date)
                .orElse(new DailySummary());

        summary.setUser(meal.getUser());
        summary.setDate(date);
        summary.setConsumedCalories(consumed);

        if (summary.getTargetCalories() == null) {
            summary.setTargetCalories(2000.0);
        }

        summaryRepo.save(summary);
    }

    @Override
    @Transactional(readOnly = true)
    public AiAnalyzeResponse getResult(Long imageId) {

        Image image = imageRepo.findById(imageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Image not found")
                );

        MealItem item = image.getMealItem();

        AiAnalyzeResponse response = new AiAnalyzeResponse();
        response.setImageId(imageId);
        response.setStatus(image.getStatus());

        if (item != null) {
            response.setMealItemId(item.getId());
            response.setQuantity(item.getQuantity());
            response.setCalories(item.getCaloriesAtTime());
            response.setConfidence(item.getConfidence());

            Food food = item.getFood();
            if (food != null && !food.getName().equals("__PROCESSING__")) {
                response.setFoodName(food.getName());
            }
        }

        return response;
    }
}