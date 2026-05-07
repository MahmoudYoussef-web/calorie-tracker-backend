package com.caloriestracker.system.service.workout;

import com.caloriestracker.system.dto.response.workout.WorkoutLogResponse;

import java.time.LocalDate;

public interface WorkoutService {

    WorkoutLogResponse logWorkout(Long userId, LocalDate date);

    void removeWorkoutLog(Long userId, LocalDate date);
}