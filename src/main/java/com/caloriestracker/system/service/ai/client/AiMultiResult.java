package com.caloriestracker.system.service.ai.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiMultiResult {
    private List<AiResult> items;
}