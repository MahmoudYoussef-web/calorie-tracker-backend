package com.caloriestracker.system.config;

import com.caloriestracker.system.service.ai.provider.AiVisionProvider;
import com.caloriestracker.system.service.ai.provider.HuggingFaceVisionProvider;
import com.caloriestracker.system.service.ai.provider.MockVisionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AiProviderConfig {

    @Value("${ai.provider:huggingface}")
    private String activeProvider;

    private final ObjectMapper objectMapper;

    public AiProviderConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AiVisionProvider aiVisionProvider(
            HuggingFaceVisionProvider huggingFace,
            MockVisionProvider mock
    ) {
        if ("mock".equalsIgnoreCase(activeProvider)) {
            return mock;
        }
        return huggingFace;
    }
}