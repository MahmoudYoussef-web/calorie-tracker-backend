package com.caloriestracker.system.dto.request.meal;

import com.caloriestracker.system.enums.MealType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMealRequest {

    @NotNull
    private LocalDate date;

    @NotNull(message = "Please enter meal type!")
    private MealType mealType;
}
