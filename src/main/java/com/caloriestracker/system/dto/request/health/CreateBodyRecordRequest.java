package com.caloriestracker.system.dto.request.health;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class CreateBodyRecordRequest {

    @DecimalMin(value = "5.0", message = "User weight should be at least 5 kg")
    @DecimalMax(value = "200.0", message = "User weight should be at most 200 kg")
    private Double weightKg;

    @DecimalMin(value = "5.0", message = "User weight should be at least 5 kg")
    @DecimalMax(value = "200.0", message = "User weight should be at most 200 kg")
    private Double heightCm;
}
