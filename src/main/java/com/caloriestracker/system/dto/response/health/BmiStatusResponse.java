package com.caloriestracker.system.dto.response.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BmiStatusResponse {

    private Double bmi;

    private String category;

    private Double dailyCalories;

    private Double heightCm;

    private Double weightKg;
}