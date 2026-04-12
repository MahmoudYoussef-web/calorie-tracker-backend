package com.caloriestracker.system.dto.request.health;

import com.caloriestracker.system.enums.ActivityLevel;
import com.caloriestracker.system.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BmiRequest {

    @NotNull
    private Gender gender;

    @Min(value = 5, message = "User age should be greater than 1 year")
    @Max(value = 100, message = "User age should be less than 100 year")
    private Integer age;

    @DecimalMin(value = "50.0", message = "User height should be more than 50 cm")
    @DecimalMax(value = "250.0",  message = "User height should be less than 250 cm")
    private Double heightCm;

    @DecimalMin(value = "5.0", message = "User weight should be greater than 5 kg")
    @DecimalMax(value = "200.0", message = "User weight should be less than 200 kg")
    private Double weightKg;

    @NotNull
    private ActivityLevel activityLevel;
}
