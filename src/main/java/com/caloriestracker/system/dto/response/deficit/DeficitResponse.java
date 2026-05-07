package com.caloriestracker.system.dto.response.deficit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeficitResponse {

    private Double targetCalories;

    private Double maintenanceCalories;

    private Double dailyDeficit;

    private Double weightLossPaceKgPerWeek;
}