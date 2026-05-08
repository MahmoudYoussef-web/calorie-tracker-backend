package com.caloriestracker.system.config;

import com.caloriestracker.system.service.ai.provider.AiVisionProvider;
import com.caloriestracker.system.service.ai.provider.HuggingFaceVisionProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AiProviderConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);
        factory.setReadTimeout(120_000);
        return new RestTemplate(factory);
    }

    @Bean
    public AiVisionProvider aiVisionProvider(
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        return new HuggingFaceVisionProvider(restTemplate, objectMapper);
    }
}