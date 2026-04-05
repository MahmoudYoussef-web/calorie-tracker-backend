package com.caloriestracker.system.dto.request.deficit;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CalorieDeficitRequest {

    @NotNull
    @Min(0)
    @Max(1500)
    private Integer deficit;
}