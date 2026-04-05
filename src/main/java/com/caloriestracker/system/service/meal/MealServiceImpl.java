package com.caloriestracker.system.service.meal;

import com.caloriestracker.system.dto.request.meal.AddMealItemRequest;
import com.caloriestracker.system.dto.request.meal.CreateMealRequest;
import com.caloriestracker.system.dto.request.meal.ManualMealItemRequest;
import com.caloriestracker.system.dto.response.meal.MealResponse;
import com.caloriestracker.system.entity.*;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.mapper.MealMapper;
import com.caloriestracker.system.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepo;
    private final MealItemRepository itemRepo;
    private final FoodRepository foodRepo;
    private final UserRepository userRepo;
    private final MealMapper mealMapper;
    private final DailySummaryRepository summaryRepo;

    @Override
    @Transactional
    public MealResponse createMeal(Long userId, CreateMealRequest request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        mealRepo.findByUser_IdAndMealDateAndMealType(
                userId,
                request.getDate(),
                request.getMealType()
        ).ifPresent(m -> {
            throw new BadRequestException("Meal already exists");
        });

        Meal meal = Meal.builder()
                .mealDate(request.getDate())
                .mealType(request.getMealType())
                .user(user)
                .build();

        return mealMapper.toResponse(mealRepo.save(meal));
    }

    @Override
    @Transactional
    public MealResponse addItem(Long userId, Long mealId, AddMealItemRequest request) {

        validateQuantity(request.getQuantity());

        Meal meal = mealRepo.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found"));

        checkAccess(meal, userId);

        Food food = foodRepo.findById(request.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        MealItem item = buildItem(meal, food, request.getQuantity());

        itemRepo.save(item);

        updateSummary(meal);

        return mealMapper.toResponse(meal);
    }

    @Override
    @Transactional
    public MealResponse updateItem(Long userId, Long itemId, AddMealItemRequest request) {

        validateQuantity(request.getQuantity());

        MealItem item = itemRepo
                .findByIdAndMeal_User_Id(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        Food food = foodRepo.findById(request.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        item.setFood(food);
        item.setQuantity(request.getQuantity());
        item.setCaloriesAtTime(food.getCalories() * request.getQuantity());
        item.setProteinAtTime(food.getProtein() * request.getQuantity());
        item.setCarbsAtTime(food.getCarbs() * request.getQuantity());
        item.setFatAtTime(food.getFat() * request.getQuantity());

        updateSummary(item.getMeal());

        return mealMapper.toResponse(item.getMeal());
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {

        MealItem item = itemRepo
                .findByIdAndMeal_User_Id(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        Meal meal = item.getMeal();

        meal.getItems().remove(item);
        itemRepo.delete(item);

        updateSummary(meal);
    }

    @Override
    @Transactional(readOnly = true)
    public MealResponse getMeal(Long userId, Long mealId) {

        Meal meal = mealRepo.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found"));

        checkAccess(meal, userId);

        return mealMapper.toResponse(meal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealResponse> getMealsByDate(Long userId, LocalDate date) {

        return mealRepo.findByUser_IdAndMealDate(userId, date)
                .stream()
                .map(mealMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getDailyCalories(Long userId, LocalDate date) {

        return mealRepo.findByUser_IdAndMealDate(userId, date)
                .stream()
                .flatMap(m -> m.getItems() == null ? Stream.empty() : m.getItems().stream())
                .mapToDouble(i -> i.getCaloriesAtTime() == null ? 0.0 : i.getCaloriesAtTime())
                .sum();
    }

    private void validateQuantity(Double q) {
        if (q == null || q <= 0) {
            throw new BadRequestException("Invalid quantity");
        }
    }

    private void checkAccess(Meal meal, Long userId) {
        if (!meal.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }
    }

    private MealItem buildItem(Meal meal, Food food, Double quantity) {

        MealItem item = MealItem.builder()
                .food(food)
                .quantity(quantity)
                .caloriesAtTime(food.getCalories() * quantity)
                .proteinAtTime(food.getProtein() * quantity)
                .carbsAtTime(food.getCarbs() * quantity)
                .fatAtTime(food.getFat() * quantity)
                .build();

        meal.addItem(item);

        return item;
    }

    private void updateSummary(Meal meal) {

        LocalDate date = meal.getMealDate();
        Long userId = meal.getUser().getId();

        double consumed = mealRepo
                .findByUser_IdAndMealDate(userId, date)
                .stream()
                .flatMap(m -> m.getItems() == null ? Stream.empty() : m.getItems().stream())
                .mapToDouble(i -> i.getCaloriesAtTime() == null ? 0.0 : i.getCaloriesAtTime())
                .sum();

        DailySummary summary = summaryRepo
                .findByUserIdAndDate(userId, date)
                .orElse(
                        DailySummary.builder()
                                .user(meal.getUser())
                                .date(date)
                                .build()
                );

        summary.setConsumedCalories(consumed);

        if (summary.getTargetCalories() == null) {
            summary.setTargetCalories(0.0);
        }

        summaryRepo.save(summary);
    }

    @Override
    @Transactional
    public MealResponse addManualItem(Long userId, Long mealId, ManualMealItemRequest request) {

        validateQuantity(request.getQuantity());

        Meal meal = mealRepo.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found"));

        checkAccess(meal, userId);

        Food food = foodRepo.findByNameIgnoreCase(request.getName())
                .orElseGet(() -> foodRepo.save(
                        Food.builder()
                                .name(request.getName())
                                .calories(request.getCalories())
                                .protein(request.getProtein())
                                .carbs(request.getCarbs())
                                .fat(request.getFat())
                                .build()
                ));

        MealItem item = MealItem.builder()
                .food(food)
                .quantity(request.getQuantity())
                .caloriesAtTime(request.getCalories() * request.getQuantity())
                .proteinAtTime(request.getProtein() * request.getQuantity())
                .carbsAtTime(request.getCarbs() * request.getQuantity())
                .fatAtTime(request.getFat() * request.getQuantity())
                .confidence(1.0)
                .build();

        meal.addItem(item);

        itemRepo.save(item);

        updateSummary(meal);

        return mealMapper.toResponse(meal);
    }
}