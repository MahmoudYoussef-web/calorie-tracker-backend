package com.caloriestracker.system.service.ai;

import com.caloriestracker.system.dto.response.ai.AiAnalyzeResponse;
import com.caloriestracker.system.entity.*;
import com.caloriestracker.system.enums.ImageStatus;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.repository.*;
import com.caloriestracker.system.service.ai.client.AiResult;
import com.caloriestracker.system.service.ai.provider.AiVisionProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.stream.Stream;

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

        // Create Placeholder MealItem
        MealItem item = MealItem.builder()
                .meal(meal)
                .image(image)
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

        Image image = imageRepo.findById(imageId)
                .orElseThrow();

        MealItem item = image.getMealItem();

        try {

            AiResult result = visionProvider.analyze(file);

            Food food = foodRepo.findByNameIgnoreCase(result.getName())
                    .orElseGet(() ->
                            foodRepo.save(
                                    Food.builder()
                                            .name(result.getName())
                                            .calories(result.getCalories())
                                            .protein(0.0)
                                            .carbs(0.0)
                                            .fat(0.0)
                                            .build()
                            )
                    );


            item.setFood(food);
            item.setQuantity(result.getQuantity());

            item.setCaloriesAtTime(result.getCalories());
            item.setProteinAtTime(food.getProtein());
            item.setCarbsAtTime(food.getCarbs());
            item.setFatAtTime(food.getFat());

            item.setConfidence(result.getConfidence());

            itemRepo.save(item);

            updateSummary(item.getMeal());

            image.setStatus(ImageStatus.DONE);

        } catch (Exception e) {

            image.setStatus(ImageStatus.FAILED);
        }

        imageRepo.save(image);
    }


    @Override
    @Transactional
    public void retry(Long imageId) {

        Image image = imageRepo.findById(imageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Image not found")
                );

        if (image.getStatus() != ImageStatus.FAILED) {
            throw new BadRequestException("Not failed image");
        }

        image.setStatus(ImageStatus.PROCESSING);
        imageRepo.save(image);

        processAsync(image.getId(), null);
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

        summaryRepo.save(summary);
    }
}