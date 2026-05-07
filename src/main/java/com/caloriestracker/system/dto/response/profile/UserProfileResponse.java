package com.caloriestracker.system.dto.response.profile;

import com.caloriestracker.system.enums.ActivityLevel;
import com.caloriestracker.system.enums.Gender;
import com.caloriestracker.system.enums.Goal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;

    private Gender gender;
    private Integer age;

    private Double heightCm;
    private Double weightKg;
    private Double targetWeightKg;

    private Goal goal;
    private ActivityLevel activityLevel;

    private Double dailyCalories;

    private Integer weeklyExerciseDays;
    private String preferredExercise;
    private Integer workoutDuration;
}