package com.caloriestracker.system.dto.response.progress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseProgressResponse {

    private Integer actualWeeklyExerciseDays;

    private Integer targetWeeklyExerciseDays;

    private Double progressPercent;
}