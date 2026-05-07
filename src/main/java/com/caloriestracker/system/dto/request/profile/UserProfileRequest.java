package com.caloriestracker.system.dto.request.profile;

import com.caloriestracker.system.enums.ActivityLevel;
import com.caloriestracker.system.enums.Gender;
import com.caloriestracker.system.enums.Goal;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserProfileRequest {

    @NotNull(message = "please enter gender type [MALE, FEMALE]")
    private Gender gender;

    @Min(value = 5, message = "User age should be greater than 5 years")
    @Max(value = 100, message = "User age should be less than 100 years")
    private Integer age;

    @DecimalMin(value = "50.0", message = "User height should be more than 50 cm")
    @DecimalMax(value = "250.0", message = "User height should be less than 250 cm")
    private Double heightCm;

    @DecimalMin(value = "5.0", message = "User weight should be greater than 5 kg")
    @DecimalMax(value = "200.0", message = "User weight should be less than 200 kg")
    private Double weightKg;

    @DecimalMin(value = "5.0", message = "Target weight should be greater than 5 kg")
    @DecimalMax(value = "200.0", message = "Target weight should be less than 200 kg")
    private Double targetWeightKg;

    @NotNull(message = "Please enter your goal [LOSE, MAINTAIN, GAIN]")
    private Goal goal;

    @NotNull
    private ActivityLevel activityLevel;

    @Min(value = 0, message = "Weekly exercise days must be between 0 and 7")
    @Max(value = 7, message = "Weekly exercise days must be between 0 and 7")
    private Integer weeklyExerciseDays;

    @Size(max = 100, message = "Preferred exercise description too long")
    private String preferredExercise;

    @Min(value = 0, message = "Workout duration cannot be negative")
    private Integer workoutDuration;
}