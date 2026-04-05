package com.caloriestracker.system.service.common;

import com.caloriestracker.system.enums.ActivityLevel;
import com.caloriestracker.system.enums.Gender;
import com.caloriestracker.system.enums.Goal;
import com.caloriestracker.system.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class CalculationServiceImpl implements CalculationService {

    @Override
    public double calculateBmr(
            Gender gender,
            int age,
            double heightCm,
            double weightKg
    ) {

        if (gender == null || age <= 0 || heightCm <= 0 || weightKg <= 0) {
            throw new BadRequestException("Invalid BMR input");
        }

        if (gender == Gender.MALE) {
            return 10 * weightKg
                    + 6.25 * heightCm
                    - 5 * age
                    + 5;
        }

        return 10 * weightKg
                + 6.25 * heightCm
                - 5 * age
                - 161;
    }

    @Override
    public double calculateTdee(double bmr, ActivityLevel level) {

        if (level == null) {
            throw new BadRequestException("Activity level is required");
        }

        return switch (level) {
            case SEDENTARY -> bmr * 1.2;
            case LIGHT -> bmr * 1.375;
            case MODERATE -> bmr * 1.55;
            case ACTIVE -> bmr * 1.725;
            case VERY_ACTIVE -> bmr * 1.9;
        };
    }

    @Override
    public double calculateTargetCalories(
            double tdee,
            Goal goal
    ) {

        if (goal == null) {
            throw new BadRequestException("Goal is required");
        }

        return switch (goal) {
            case LOSE -> tdee - 500;
            case MAINTAIN -> tdee;
            case GAIN -> tdee + 400;
        };
    }
}