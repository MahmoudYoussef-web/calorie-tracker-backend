package com.caloriestracker.system.service.ai;

import com.caloriestracker.system.dto.response.ai.AiAnalyzeResponse;
import com.caloriestracker.system.enums.ImageStatus;
import org.springframework.web.multipart.MultipartFile;

public interface AiService {

    AiAnalyzeResponse analyze(Long mealId, MultipartFile file);

    void processAsync(Long imageId, MultipartFile file);

    void retry(Long imageId);

    ImageStatus getStatus(Long imageId);


    AiAnalyzeResponse getResult(Long imageId);
}