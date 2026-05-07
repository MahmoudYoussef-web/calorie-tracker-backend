package com.caloriestracker.system.service.deficit;

import com.caloriestracker.system.dto.request.deficit.CalorieDeficitRequest;
import com.caloriestracker.system.dto.response.deficit.DeficitProjectionResponse;
import com.caloriestracker.system.dto.response.deficit.DeficitResponse;

public interface DeficitService {

    void setDeficit(Long userId, CalorieDeficitRequest request);

    DeficitResponse getDeficit(Long userId);

    DeficitProjectionResponse getProjection(Long userId);
}