package com.caloriestracker.system.dto.response.food;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodResponse {

    private Long id;

    private String name;

    private Double calories;

    private Double protein;

    private Double carbs;

    private Double fat;

    private String imageUrl;
}