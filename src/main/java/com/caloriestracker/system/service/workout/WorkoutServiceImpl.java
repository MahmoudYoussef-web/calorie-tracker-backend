package com.caloriestracker.system.service.workout;

import com.caloriestracker.system.dto.response.workout.WorkoutLogResponse;
import com.caloriestracker.system.entity.User;
import com.caloriestracker.system.entity.UserProfile;
import com.caloriestracker.system.entity.WorkoutLog;
import com.caloriestracker.system.exception.BadRequestException;
import com.caloriestracker.system.exception.ResourceNotFoundException;
import com.caloriestracker.system.repository.UserProfileRepository;
import com.caloriestracker.system.repository.UserRepository;
import com.caloriestracker.system.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutLogRepository workoutLogRepo;
    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;

    @Override
    @Transactional
    public WorkoutLogResponse logWorkout(Long userId, LocalDate date) {

        LocalDate workoutDate = (date != null) ? date : LocalDate.now();

        UserProfile profile = profileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getWeeklyExerciseDays() == null
                || profile.getWeeklyExerciseDays() == 0
                || profile.getExerciseWeekStartDate() == null) {
            throw new BadRequestException(
                    "Please set your weekly exercise days in your profile first");
        }

        LocalDate weekStart = getCurrentWeekStart(profile);
        LocalDate weekEnd   = weekStart.plusDays(6);

        if (workoutDate.isBefore(weekStart) || workoutDate.isAfter(weekEnd)) {
            throw new BadRequestException(
                    "Date must be within the current week: " + weekStart + " to " + weekEnd);
        }

        return workoutLogRepo.findByUser_IdAndWorkoutDate(userId, workoutDate)
                .map(existing -> WorkoutLogResponse.builder()
                        .id(existing.getId())
                        .workoutDate(existing.getWorkoutDate())
                        .weekStart(weekStart)
                        .weekEnd(weekEnd)
                        .message("Workout already logged for this day")
                        .build())
                .orElseGet(() -> {
                    User user = userRepo.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                    WorkoutLog saved = workoutLogRepo.save(
                            WorkoutLog.builder()
                                    .user(user)
                                    .workoutDate(workoutDate)
                                    .build()
                    );

                    return WorkoutLogResponse.builder()
                            .id(saved.getId())
                            .workoutDate(saved.getWorkoutDate())
                            .weekStart(weekStart)
                            .weekEnd(weekEnd)
                            .message("Workout logged successfully")
                            .build();
                });
    }

    @Override
    @Transactional
    public void removeWorkoutLog(Long userId, LocalDate date) {

        LocalDate workoutDate = (date != null) ? date : LocalDate.now();

        WorkoutLog log = workoutLogRepo.findByUser_IdAndWorkoutDate(userId, workoutDate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No workout log found for date: " + workoutDate));

        workoutLogRepo.delete(log);
    }


    public static LocalDate getCurrentWeekStart(UserProfile profile) {
        LocalDate start = profile.getExerciseWeekStartDate();
        LocalDate today = LocalDate.now();
        long daysSinceStart = ChronoUnit.DAYS.between(start, today);
        long weeksPassed    = daysSinceStart / 7;
        return start.plusDays(weeksPassed * 7);
    }
}