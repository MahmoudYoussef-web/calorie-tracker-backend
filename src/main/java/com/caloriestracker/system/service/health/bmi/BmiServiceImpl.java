package com.caloriestracker.system.service.health.bmi;

import com.caloriestracker.system.dto.request.health.BmiRequest;
import com.caloriestracker.system.dto.response.health.BmiResponse;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.service.common.CalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BmiServiceImpl implements BmiService {

    private final CalculationService calculationService;

    @Override
    public BmiResponse calculate(BmiRequest request) {

        if (request.getHeightCm() == null || request.getHeightCm() <= 0) {
            throw new BadRequestException("Invalid height");
        }

        if (request.getWeightKg() == null || request.getWeightKg() <= 0) {
            throw new BadRequestException("Invalid weight");
        }

        double heightM = request.getHeightCm() / 100.0;

        double bmi =
                request.getWeightKg() /
                        (heightM * heightM);

        String category;

        if (bmi < 18.5) {
            category = "Underweight";
        } else if (bmi < 25) {
            category = "Normal";
        } else if (bmi < 30) {
            category = "Overweight";
        } else {
            category = "Obese";
        }

        double bmr = calculationService.calculateBmr(
                request.getGender(),
                request.getAge(),
                request.getHeightCm(),
                request.getWeightKg()
        );

        double tdee = calculationService.calculateTdee(
                bmr,
                request.getActivityLevel()
        );

        BmiResponse response = new BmiResponse();

        response.setBmi(Math.round(bmi * 100.0) / 100.0);
        response.setCategory(category);
        response.setDailyCalories(
                (double) Math.round(tdee)
        );

        return response;
    }
}