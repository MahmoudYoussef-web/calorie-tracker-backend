package com.caloriestracker.system.dto.response.deficit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeficitProjectionResponse {

    private Double targetCalories;

    private Double maintenanceCalories;

    private Double dailyDeficit;

    private Double currentWeightKg;

    private Double targetWeightKg;

    private Double weightToLoseKg;

    private Double weightLossPaceKgPerWeek;

    private Integer timeToGoalWeeks;
}