package com.caloriestracker.system.controller.workout;

import com.caloriestracker.system.dto.response.workout.WorkoutLogResponse;
import com.caloriestracker.system.service.workout.WorkoutService;
import com.caloriestracker.system.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;
    private final AuthUtils authUtils;

    @PostMapping("/log")
    public ResponseEntity<WorkoutLogResponse> logWorkout(
            Authentication authentication,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        Long userId = authUtils.getUserId(authentication);
        return ResponseEntity.ok(
                workoutService.logWorkout(userId, date)
        );
    }

    @DeleteMapping("/log")
    public ResponseEntity<Void> removeWorkoutLog(
            Authentication authentication,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        Long userId = authUtils.getUserId(authentication);
        workoutService.removeWorkoutLog(userId, date);
        return ResponseEntity.ok().build();
    }
}