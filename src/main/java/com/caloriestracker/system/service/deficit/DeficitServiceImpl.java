package com.caloriestracker.system.service.deficit;

import com.caloriestracker.system.dto.request.deficit.CalorieDeficitRequest;
import com.caloriestracker.system.dto.response.deficit.DeficitProjectionResponse;
import com.caloriestracker.system.dto.response.deficit.DeficitResponse;
import com.caloriestracker.system.entity.User;
import com.caloriestracker.system.entity.UserDeficit;
import com.caloriestracker.system.entity.UserProfile;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.repository.UserDeficitRepository;
import com.caloriestracker.system.repository.UserProfileRepository;
import com.caloriestracker.system.repository.UserRepository;
import com.caloriestracker.system.service.common.CalculationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeficitServiceImpl implements DeficitService {

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final UserDeficitRepository deficitRepo;
    private final CalculationService calculationService;

    private static final double CALORIES_PER_KG = 7700.0;

    @Override
    public void setDeficit(Long userId, CalorieDeficitRequest request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Integer deficitInput = request.getDeficit();
        if (deficitInput == null || deficitInput < 0 || deficitInput > 1500)
            throw new BadRequestException("Invalid deficit value");

        double deficitValue = deficitInput.doubleValue();

        double bmr = calculationService.calculateBmr(
                profile.getGender(), profile.getAge(),
                profile.getHeightCm(), profile.getWeightKg());

        double tdee = calculationService.calculateTdee(bmr, profile.getActivityLevel());
        double targetCalories = tdee - deficitValue;

        if (targetCalories < 0)
            throw new BadRequestException("Deficit too high");


        double weeklyDeficit = deficitValue * 7;
        double pace = Math.round((weeklyDeficit / CALORIES_PER_KG) * 100.0) / 100.0;

        UserDeficit deficit = deficitRepo.findByUser_Id(userId).orElse(null);
        if (deficit == null) {
            deficit = new UserDeficit();
            deficit.setUser(user);
        }

        deficit.setMaintenanceCalories(tdee);
        deficit.setDeficitCalories(deficitValue);
        deficit.setTargetCalories(targetCalories);
        deficit.setWeightLossPaceKgPerWeek(pace);

        deficitRepo.saveAndFlush(deficit);
    }

    @Override
    @Transactional(readOnly = true)
    public DeficitResponse getDeficit(Long userId) {

        UserDeficit deficit = deficitRepo.findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No deficit configured for this user"
                        )
                );

        return DeficitResponse.builder()
                .targetCalories(deficit.getTargetCalories())
                .maintenanceCalories(deficit.getMaintenanceCalories())
                .dailyDeficit(deficit.getDeficitCalories())
                .weightLossPaceKgPerWeek(deficit.getWeightLossPaceKgPerWeek())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DeficitProjectionResponse getProjection(Long userId) {

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found")
                );

        UserDeficit deficit = deficitRepo.findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No deficit configured. Please set your deficit first."
                        )
                );

        Double currentWeight = profile.getWeightKg();
        Double targetWeight  = profile.getTargetWeightKg();

        if (targetWeight == null) {
            throw new BadRequestException(
                    "Target weight not set in profile. " +
                            "Please update your profile with a target weight."
            );
        }

        double weightToLose = Math.abs(currentWeight - targetWeight);

        double pace = deficit.getWeightLossPaceKgPerWeek() != null
                ? deficit.getWeightLossPaceKgPerWeek()
                : 0.0;

        Integer timeToGoalWeeks = null;
        if (pace > 0) {
            timeToGoalWeeks = (int) Math.ceil(weightToLose / pace);
        }

        return DeficitProjectionResponse.builder()
                .targetCalories(deficit.getTargetCalories())
                .maintenanceCalories(deficit.getMaintenanceCalories())
                .dailyDeficit(deficit.getDeficitCalories())
                .currentWeightKg(currentWeight)
                .targetWeightKg(targetWeight)
                .weightToLoseKg(Math.round(weightToLose * 100.0) / 100.0)
                .weightLossPaceKgPerWeek(pace)
                .timeToGoalWeeks(timeToGoalWeeks)
                .build();
    }
}