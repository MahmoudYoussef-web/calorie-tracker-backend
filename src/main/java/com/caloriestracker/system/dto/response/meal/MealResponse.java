package com.caloriestracker.system.dto.response.meal;

import com.caloriestracker.system.enums.MealType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealResponse {

    private Long id;

    private LocalDate date;

    private MealType mealType;

    private List<MealItemResponse> items;
}