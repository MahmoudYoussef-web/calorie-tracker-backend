package com.caloriestracker.system.service.health;

import com.caloriestracker.system.dto.request.deficit.CalorieDeficitRequest;
import com.caloriestracker.system.dto.response.dashboard.CaloriesProgressResponse;
import com.caloriestracker.system.dto.response.progress.ExerciseProgressResponse;
import com.caloriestracker.system.dto.response.progress.WeightProgressResponse;

import java.util.List;

public interface ProgressService {

    List<CaloriesProgressResponse> getCalories(Long userId);

    void setDeficit(Long userId, CalorieDeficitRequest request);

    WeightProgressResponse getWeightProgress(Long userId);

    ExerciseProgressResponse getExerciseProgress(Long userId);
}