package com.caloriestracker.system.controller.food;

import com.caloriestracker.system.dto.request.meal.AddMealItemRequest;
import com.caloriestracker.system.dto.request.meal.CreateMealRequest;
import com.caloriestracker.system.dto.request.meal.ManualMealItemRequest;
import com.caloriestracker.system.dto.response.meal.MealResponse;
import com.caloriestracker.system.service.meal.MealService;
import com.caloriestracker.system.util.AuthUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;
    private final AuthUtils authUtils;

    @PostMapping
    public ResponseEntity<MealResponse> createMeal(
            @Valid @RequestBody CreateMealRequest request,
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        MealResponse response = mealService.createMeal(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{mealId}/items")
    public ResponseEntity<MealResponse> addItem(
            @PathVariable Long mealId,
            @Valid @RequestBody AddMealItemRequest request,
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        MealResponse response =
                mealService.addItem(userId, mealId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{mealId}/items/manual")
    public ResponseEntity<MealResponse> addManualItem(
            @PathVariable Long mealId,
            @Valid @RequestBody ManualMealItemRequest request,
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        MealResponse response =
                mealService.addManualItem(userId, mealId, request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<MealResponse> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody AddMealItemRequest request,
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        MealResponse response =
                mealService.updateItem(userId, itemId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long itemId,
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        mealService.deleteItem(userId, itemId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{mealId}")
    public ResponseEntity<MealResponse> getMeal(
            @PathVariable Long mealId,
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        MealResponse response =
                mealService.getMeal(userId, mealId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<MealResponse>> getMealsByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        List<MealResponse> response =
                mealService.getMealsByDate(userId, date);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily-calories")
    public ResponseEntity<Double> getDailyCalories(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        LocalDate targetDate =
                date != null ? date : LocalDate.now();

        Double response =
                mealService.getDailyCalories(userId, targetDate);

        return ResponseEntity.ok(response);
    }
}