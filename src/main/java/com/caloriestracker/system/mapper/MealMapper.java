package com.caloriestracker.system.mapper;

import com.caloriestracker.system.dto.response.meal.MealItemResponse;
import com.caloriestracker.system.dto.response.meal.MealResponse;
import com.caloriestracker.system.entity.Meal;
import com.caloriestracker.system.entity.MealItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MealMapper {

    public MealResponse toResponse(Meal meal) {

        List<MealItemResponse> items =
                meal.getItems() == null
                        ? List.of()
                        : meal.getItems().stream()
                        .map(this::toItemResponse)
                        .toList();

        return MealResponse.builder()
                .id(meal.getId())
                .date(meal.getMealDate())
                .mealType(meal.getMealType())
                .items(items)
                .build();
    }

    private MealItemResponse toItemResponse(MealItem item) {

        if (item.getFood() == null) {
            return MealItemResponse.builder()
                    .quantity(item.getQuantity())
                    .calories(item.getCaloriesAtTime())
                    .build();
        }

        return MealItemResponse.builder()
                .foodId(item.getFood().getId())
                .foodName(item.getFood().getName())
                .quantity(item.getQuantity())
                .calories(item.getCaloriesAtTime())
                .confidence(item.getConfidence())
                .imageId(
                        item.getImage() != null
                                ? item.getImage().getId()
                                : null
                )
                .build();
    }
}