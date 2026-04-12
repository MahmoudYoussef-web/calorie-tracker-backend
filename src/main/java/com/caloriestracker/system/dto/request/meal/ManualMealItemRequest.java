package com.caloriestracker.system.dto.request.meal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ManualMealItemRequest {

    @NotBlank (message = "Please enter item name!")
    private String name;

    @NotNull
    @DecimalMin(value = "0.0", message = "Calories should not be less than 0 calory")
    private Double calories;

    @NotNull
    @DecimalMin(value = "0.0", message = "Proteins should not be less than 0 protein")
    private Double protein;

    @NotNull
    @DecimalMin(value = "0.0", message = "Carbs should not be less than 0 carb")
    private Double carbs;

    @NotNull
    @DecimalMin(value = "0.0", message = "Fats should not be less than 0 fat")
    private Double fat;

    @NotNull
    @DecimalMin(value = "0.1", message = "Quantity should be more than 100g")
    private Double quantity;
}