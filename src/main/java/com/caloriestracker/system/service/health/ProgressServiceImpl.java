package com.caloriestracker.system.service.health;

import com.caloriestracker.system.dto.request.deficit.CalorieDeficitRequest;
import com.caloriestracker.system.dto.response.dashboard.CaloriesProgressResponse;
import com.caloriestracker.system.dto.response.progress.ExerciseProgressResponse;
import com.caloriestracker.system.dto.response.progress.WeightProgressResponse;
import com.caloriestracker.system.entity.UserProfile;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.repository.MealRepository;
import com.caloriestracker.system.repository.UserDeficitRepository;
import com.caloriestracker.system.repository.UserProfileRepository;
import com.caloriestracker.system.repository.WorkoutLogRepository;
import com.caloriestracker.system.service.deficit.DeficitService;
import com.caloriestracker.system.service.workout.WorkoutServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final MealRepository mealRepository;
    private final DeficitService deficitService;
    private final UserProfileRepository profileRepo;
    private final UserDeficitRepository deficitRepo;
    private final WorkoutLogRepository workoutLogRepo;

    @Override
    public List<CaloriesProgressResponse> getCalories(Long userId) {
        return mealRepository.findCaloriesProgress(
                userId,
                LocalDate.now().minusDays(6),
                LocalDate.now()
        );
    }

    @Override
    @Transactional
    public void setDeficit(Long userId, CalorieDeficitRequest request) {
        deficitService.setDeficit(userId, request);
    }

    @Override
    public WeightProgressResponse getWeightProgress(Long userId) {

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Double targetWeightKg = profile.getTargetWeightKg();

        if (targetWeightKg == null) {
            return WeightProgressResponse.builder()
                    .currentWeightKg(profile.getWeightKg())
                    .targetWeightKg(null)
                    .weightToLoseKg(null)
                    .progressPercent(0.0)
                    .build();
        }

        double currentWeight = profile.getWeightKg();
        double weightToLose  = Math.max(0, currentWeight - targetWeightKg);

        if (weightToLose <= 0) {
            return WeightProgressResponse.builder()
                    .currentWeightKg(currentWeight)
                    .targetWeightKg(targetWeightKg)
                    .weightToLoseKg(0.0)
                    .progressPercent(100.0)
                    .build();
        }

        return WeightProgressResponse.builder()
                .currentWeightKg(currentWeight)
                .targetWeightKg(targetWeightKg)
                .weightToLoseKg(Math.round(weightToLose * 100.0) / 100.0)
                .progressPercent(0.0)
                .build();
    }

    @Override
    public ExerciseProgressResponse getExerciseProgress(Long userId) {

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Integer targetDays = profile.getWeeklyExerciseDays();

        if (targetDays == null || targetDays == 0
                || profile.getExerciseWeekStartDate() == null) {
            return ExerciseProgressResponse.builder()
                    .targetWeeklyExerciseDays(targetDays != null ? targetDays : 0)
                    .actualWeeklyExerciseDays(0)
                    .progressPercent(0.0)
                    .build();
        }

        LocalDate weekStart = WorkoutServiceImpl.getCurrentWeekStart(profile);
        LocalDate weekEnd   = weekStart.plusDays(6);

        int actualDays = workoutLogRepo
                .findByUserIdAndDateBetween(userId, weekStart, weekEnd)
                .size();

        double progressPercent = Math.min(
                ((double) actualDays / targetDays) * 100.0,
                100.0
        );

        return ExerciseProgressResponse.builder()
                .targetWeeklyExerciseDays(targetDays)
                .actualWeeklyExerciseDays(actualDays)
                .progressPercent(Math.round(progressPercent * 100.0) / 100.0)
                .build();
    }
}