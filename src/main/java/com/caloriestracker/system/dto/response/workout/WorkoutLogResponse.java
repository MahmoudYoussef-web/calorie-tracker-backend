package com.caloriestracker.system.dto.response.workout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogResponse {

    private Long id;

    private LocalDate workoutDate;

    private LocalDate weekStart;


    private LocalDate weekEnd;

    private String message;
}