package com.caloriestracker.system.dto.request.deficit;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CalorieDeficitRequest {

    @NotNull
    @Min(value = 0, message = "Deficit should be greater than 0")
    @Max(value = 1500, message = "Deficit should be less than 1500")
    private Integer deficit;
}