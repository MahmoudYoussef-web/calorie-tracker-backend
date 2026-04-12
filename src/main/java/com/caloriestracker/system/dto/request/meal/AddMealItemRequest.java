package com.caloriestracker.system.dto.request.meal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMealItemRequest {

    @NotNull
    private Long foodId;

    @NotNull
    @DecimalMin(value = "0.1", message = "Quantity should be more than 100g")
    private Double quantity;
}