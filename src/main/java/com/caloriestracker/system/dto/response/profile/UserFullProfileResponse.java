package com.caloriestracker.system.dto.response.profile;

import com.caloriestracker.system.enums.ActivityLevel;
import com.caloriestracker.system.enums.Gender;
import com.caloriestracker.system.enums.Goal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFullProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String displayName;
    private String fullName;
    private String email;
    private LocalDateTime joinedAt;


    private Gender gender;
    private Integer age;
    private Double heightCm;
    private Double currentWeightKg;
    private Double targetWeightKg;
    private Goal goal;
    private ActivityLevel activityLevel;

    private Double dailyCalorieGoal;
    private Double dailyDeficit;
    private String weightLossPace;
    private String weightLossPaceId;

    private String bmiCategory;

    private Integer timeToGoalWeeks;

    private String weeklyExerciseDays;
    private String exerciseDays;
    private String preferredExercise;
    private String workoutDuration;

    private Double goalReachTargetWeight;
    private Double goalWeeklyExercise;

    private ProfileExtras profileExtras;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileExtras {
        private Double dailyCalorieGoal;
        private Double targetWeightKg;
        private Double dailyDeficit;
        private String weightLossPace;
        private String weightLossPaceId;
        private String bmiCategory;
        private Integer timeToGoalWeeks;
        private String weeklyExerciseDays;
        private String exerciseDays;
        private String preferredExercise;
        private String workoutDuration;
        private Double goalReachTargetWeight;
        private Double goalWeeklyExercise;
    }
}