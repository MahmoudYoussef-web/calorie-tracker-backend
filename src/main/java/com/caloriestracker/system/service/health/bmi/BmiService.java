package com.caloriestracker.system.service.health.bmi;

import com.caloriestracker.system.dto.request.health.BmiRequest;
import com.caloriestracker.system.dto.response.health.BmiResponse;
import com.caloriestracker.system.dto.response.health.BmiStatusResponse;

public interface BmiService {

    BmiResponse calculate(BmiRequest request);

    BmiStatusResponse getStatusFromProfile(Long userId);
}