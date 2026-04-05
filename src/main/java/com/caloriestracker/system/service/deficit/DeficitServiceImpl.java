package com.caloriestracker.system.service.deficit;

import com.caloriestracker.system.dto.request.deficit.CalorieDeficitRequest;
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

    @Override
    public void setDeficit(Long userId, CalorieDeficitRequest request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found")
                );

        Integer deficitInput = request.getDeficit();

        if (deficitInput == null || deficitInput < 0 || deficitInput > 1500) {
            throw new BadRequestException("Invalid deficit value");
        }

        double deficitValue = deficitInput.doubleValue();

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

        double targetCalories = tdee - deficitValue;

        if (targetCalories < 0) {
            throw new BadRequestException("Deficit too high");
        }

        UserDeficit deficit = deficitRepo
                .findByUser_Id(userId)
                .orElse(null);

        if (deficit == null) {
            deficit = new UserDeficit();
            deficit.setUser(user);
        }

        deficit.setMaintenanceCalories(tdee);
        deficit.setDeficitCalories(deficitValue);
        deficit.setTargetCalories(targetCalories);

        deficitRepo.saveAndFlush(deficit);
    }
}