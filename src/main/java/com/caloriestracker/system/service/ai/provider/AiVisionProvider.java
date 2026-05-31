package com.caloriestracker.system.service.ai.provider;

import com.caloriestracker.system.service.ai.client.AiMultiResult;
import com.caloriestracker.system.service.ai.client.AiResult;
import org.springframework.web.multipart.MultipartFile;

public interface AiVisionProvider {

    AiResult analyze(MultipartFile file);

    AiMultiResult analyzeMulti(MultipartFile file);
}