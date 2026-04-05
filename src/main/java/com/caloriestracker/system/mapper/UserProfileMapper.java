package com.caloriestracker.system.mapper;

import com.caloriestracker.system.dto.response.profile.UserProfileResponse;
import com.caloriestracker.system.entity.UserProfile;
import com.caloriestracker.system.service.common.CalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileMapper {

    private final CalculationService calculationService;

    public UserProfileResponse toResponse(UserProfile p) {

        if (p == null
                || p.getGender() == null
                || p.getAge() == null
                || p.getHeightCm() == null
                || p.getWeightKg() == null
                || p.getGoal() == null
                || p.getActivityLevel() == null) {

            return UserProfileResponse.builder()
                    .id(p != null ? p.getId() : null)
                    .build();
        }

        double bmr = calculationService.calculateBmr(
                p.getGender(),
                p.getAge(),
                p.getHeightCm(),
                p.getWeightKg()
        );

        double tdee = calculationService.calculateTdee(
                bmr,
                p.getActivityLevel()
        );

        double daily = calculationService
                .calculateTargetCalories(tdee, p.getGoal());

        return UserProfileResponse.builder()
                .id(p.getId())
                .gender(p.getGender())
                .age(p.getAge())
                .heightCm(p.getHeightCm())
                .weightKg(p.getWeightKg())
                .goal(p.getGoal())
                .activityLevel(p.getActivityLevel())
                .dailyCalories(daily)
                .build();
    }
}