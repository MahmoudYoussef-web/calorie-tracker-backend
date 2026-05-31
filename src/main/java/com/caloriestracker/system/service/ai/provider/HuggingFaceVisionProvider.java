package com.caloriestracker.system.service.ai.provider;

import com.caloriestracker.system.service.ai.client.AiMultiResult;
import com.caloriestracker.system.service.ai.client.AiResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HuggingFaceVisionProvider implements AiVisionProvider {

    @Value("${ai.huggingface.url}")
    private String huggingFaceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public AiResult analyze(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No image file provided for analysis");
        }

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource imageResource = new ByteArrayResource(file.getBytes()) {

                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null
                            ? file.getOriginalFilename()
                            : "image.jpg";
                }
            };

            body.add("file", imageResource);
            body.add("diameter_px", "300");
            body.add("height_px", "120");
            body.add("ref_px", "80");

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            String endpoint = huggingFaceUrl + "/predict";

            log.info("Calling HuggingFace food detection API: {}", endpoint);

            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK
                    || response.getBody() == null) {

                throw new RuntimeException(
                        "HuggingFace API returned: " + response.getStatusCode()
                );
            }

            log.info("HuggingFace raw response: {}", response.getBody());

            return parseResponse(response.getBody());

        } catch (IOException e) {

            log.error("Failed to read image bytes", e);

            throw new RuntimeException(
                    "Failed to process image file",
                    e
            );

        } catch (Exception e) {

            log.error("HuggingFace API call failed: {}", e.getMessage());

            throw new RuntimeException(
                    "AI analysis failed: " + e.getMessage(),
                    e
            );
        }
    }
    @Override
    public AiMultiResult analyzeMulti(MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource imageResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null
                            ? file.getOriginalFilename() : "image.jpg";
                }
            };

            body.add("file", imageResource);
            body.add("ref_px", "80");
            body.add("height_ratio", "0.3");
            body.add("min_confidence", "0.7");
            body.add("max_items", "5");

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            String endpoint = huggingFaceUrl + "/predict/multi";
            log.info("Calling multi food detection: {}", endpoint);

            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint, HttpMethod.POST, request, String.class);

            return parseMultiResponse(response.getBody());

        } catch (IOException e) {
            throw new RuntimeException("Failed to process image file", e);
        }
    }

    private AiMultiResult parseMultiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode items = root.path("items");

            List<AiResult> results = new ArrayList<>();
            for (JsonNode item : items) {
                String name = item.path("name").asText();
                double confidence = item.path("confidence").asDouble(0.0);
                JsonNode nutrition = item.path("nutrition");
                double calories = nutrition.path("total_kcal").asDouble(0.0);
                double quantity = nutrition.path("mass_g").asDouble(1.0);

                results.add(new AiResult(name, calories, quantity, confidence));
            }

            return new AiMultiResult(results);

        } catch (Exception e) {
            throw new RuntimeException("Could not parse multi AI response", e);
        }
    }

    private AiResult parseResponse(String responseBody) {

        try {

            JsonNode root = objectMapper.readTree(responseBody);

            String name = root.path("top_prediction").asText();

            if (name.isBlank()) {
                throw new RuntimeException(
                        "Could not find food name in response: " + root
                );
            }

            double confidence =
                    root.path("top_confidence").asDouble(0.0);

            JsonNode nutrition = root.path("nutrition");

            double calories =
                    nutrition.path("total_kcal").asDouble(0.0);

            double massG =
                    nutrition.path("mass_g").asDouble(0.0);

            double quantity = massG > 0 ? massG : 1.0;

            log.info(
                    "AI Result → food: {}, calories: {}, quantity: {}g, confidence: {}",
                    name,
                    calories,
                    quantity,
                    confidence
            );

            return new AiResult(
                    name,
                    calories,
                    quantity,
                    confidence
            );

        } catch (Exception e) {

            log.error(
                    "Failed to parse HuggingFace response: {}",
                    responseBody,
                    e
            );

            throw new RuntimeException(
                    "Could not parse AI response",
                    e
            );
        }
    }
}