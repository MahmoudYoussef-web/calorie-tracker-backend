package com.caloriestracker.system.service.ai.client;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MockAiClient implements AiClient {

    @Override
    public AiResult analyze(MultipartFile file) {

        return new AiResult(
                "Apple",
                52.0,
                1.0,
                0.90
        );
    }
}