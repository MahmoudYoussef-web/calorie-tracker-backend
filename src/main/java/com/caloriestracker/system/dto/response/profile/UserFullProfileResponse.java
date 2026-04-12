package com.caloriestracker.system.dto.response.profile;

import com.caloriestracker.system.enums.ActivityLevel;
import com.caloriestracker.system.enums.Gender;
import com.caloriestracker.system.enums.Goal;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserFullProfileResponse {

    private Long id;


    private String fullName;
    private String email;
    private LocalDateTime joinedAt;


    private Gender gender;
    private Integer age;
    private Double heightCm;
    private Double weightKg;
    private Goal goal;
    private ActivityLevel activityLevel;
    private Double dailyCalories;
}