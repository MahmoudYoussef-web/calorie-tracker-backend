package com.caloriestracker.system.dto.response.progress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightProgressResponse {

    private Double currentWeightKg;

    private Double targetWeightKg;

    private Double weightToLoseKg;

    private Double progressPercent;
}