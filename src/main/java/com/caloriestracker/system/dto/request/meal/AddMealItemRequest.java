package com.caloriestracker.system.dto.request.meal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMealItemRequest {

    @NotNull
    private Long foodId;

    @NotNull
    @DecimalMin("0.1")
    private Double quantity;
}