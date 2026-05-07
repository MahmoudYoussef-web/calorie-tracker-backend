package com.caloriestracker.system.service.health.bmi;

import com.caloriestracker.system.dto.request.health.BmiRequest;
import com.caloriestracker.system.dto.response.health.BmiResponse;
import com.caloriestracker.system.dto.response.health.BmiStatusResponse;
import com.caloriestracker.system.entity.UserProfile;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.repository.UserProfileRepository;
import com.caloriestracker.system.service.common.CalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BmiServiceImpl implements BmiService {

    private final CalculationService calculationService;
    private final UserProfileRepository profileRepo;

    @Override
    public BmiResponse calculate(BmiRequest request) {

        if (request.getHeightCm() == null || request.getHeightCm() <= 0) {
            throw new BadRequestException("Invalid height");
        }

        if (request.getWeightKg() == null || request.getWeightKg() <= 0) {
            throw new BadRequestException("Invalid weight");
        }

        double bmi = computeBmi(request.getWeightKg(), request.getHeightCm());
        String category = getCategory(bmi);

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
        response.setDailyCalories((double) Math.round(tdee));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BmiStatusResponse getStatusFromProfile(Long userId) {

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found")
                );

        if (profile.getHeightCm() == null || profile.getWeightKg() == null) {
            throw new BadRequestException(
                    "Profile is incomplete. Please update height and weight."
            );
        }

        double bmi = computeBmi(profile.getWeightKg(), profile.getHeightCm());
        String category = getCategory(bmi);

        double bmr = calculationService.calculateBmr(
                profile.getGender(),
                profile.getAge(),
                profile.getHeightCm(),
                profile.getWeightKg()
        );

        double tdee = calculationService.calculateTdee(
                bmr,
                profile.getActivityLevel()
        );

        return BmiStatusResponse.builder()
                .bmi(Math.round(bmi * 100.0) / 100.0)
                .category(category)
                .dailyCalories((double) Math.round(tdee))
                .heightCm(profile.getHeightCm())
                .weightKg(profile.getWeightKg())
                .build();
    }

    private double computeBmi(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    private String getCategory(double bmi) {
        if (bmi < 18.5)  return "Underweight";
        if (bmi < 25.0)  return "Normal Weight";
        if (bmi < 30.0)  return "Overweight";
        return "Obese";
    }
}