package com.caloriestracker.system.config;

import com.caloriestracker.system.service.ai.provider.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiProviderConfig {

    @Bean
    public AiVisionProvider aiVisionProvider(
            MockVisionProvider mock
    ) {

        return mock;
    }
}