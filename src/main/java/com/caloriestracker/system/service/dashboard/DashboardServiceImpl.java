package com.caloriestracker.system.service.dashboard;

import com.caloriestracker.system.dto.response.dashboard.CaloriesProgressResponse;
import com.caloriestracker.system.dto.response.dashboard.DailyDashboardResponse;
import com.caloriestracker.system.entity.*;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.repository.*;
import com.caloriestracker.system.service.common.CalculationService;
import org.springframework.transaction.annotation.Propagation;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserProfileRepository profileRepo;
    private final MealRepository mealRepo;
    private final DailySummaryRepository summaryRepo;
    private final CalculationService calculationService;
    private final UserDeficitRepository deficitRepo;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public DailyDashboardResponse getDaily(Long userId, LocalDate date) {

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found")
                );

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

        double target = deficitRepo.findByUser_Id(userId)
                .map(d -> d.getTargetCalories() != null
                                ? d.getTargetCalories()
                                : calculationService.calculateTargetCalories(
                                tdee,
                                profile.getGoal()
                        )
                )
                .orElse(
                        calculationService.calculateTargetCalories(
                                tdee,
                                profile.getGoal()
                        )
                );

        List<Meal> meals =
                mealRepo.findByUser_IdAndMealDate(userId, date);

        double consumed = meals.stream()
                .flatMap(m ->
                        m.getItems() == null
                                ? Stream.empty()
                                : m.getItems().stream()
                )
                .mapToDouble(i ->
                        i.getCaloriesAtTime() == null
                                ? 0.0
                                : i.getCaloriesAtTime()
                )
                .sum();

        double remaining = target - consumed;

        String status = consumed > target ? "EXCEEDED" : "ON_TRACK";

        return new DailyDashboardResponse(
                date,
                consumed,
                target,
                remaining,
                status
        );
    }

    @Override
    public List<CaloriesProgressResponse> getWeeklyCalories(Long userId) {

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        return summaryRepo
                .findByUserIdAndDateBetweenOrderByDateAsc(
                        userId,
                        weekAgo,
                        today
                )
                .stream()
                .map(s -> new CaloriesProgressResponse(
                        s.getDate(),
                        s.getConsumedCalories() == null
                                ? 0.0
                                : s.getConsumedCalories(),
                        s.getTargetCalories() == null
                                ? 0.0
                                : s.getTargetCalories()
                ))
                .toList();
    }
}