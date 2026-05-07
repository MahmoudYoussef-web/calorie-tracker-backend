package com.caloriestracker.system.controller.progress;

import com.caloriestracker.system.dto.response.progress.ExerciseProgressResponse;
import com.caloriestracker.system.dto.response.progress.WeightProgressResponse;
import com.caloriestracker.system.service.health.ProgressService;
import com.caloriestracker.system.util.AuthUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final AuthUtils authUtils;


    @GetMapping("/weight")
    public ResponseEntity<WeightProgressResponse> getWeightProgress(
            Authentication authentication
    ) {
        Long userId = authUtils.getUserId(authentication);
        return ResponseEntity.ok(
                progressService.getWeightProgress(userId)
        );
    }

    @GetMapping("/exercise")
    public ResponseEntity<ExerciseProgressResponse> getExerciseProgress(
            Authentication authentication
    ) {
        Long userId = authUtils.getUserId(authentication);
        return ResponseEntity.ok(
                progressService.getExerciseProgress(userId)
        );
    }
}