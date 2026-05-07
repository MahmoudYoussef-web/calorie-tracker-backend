package com.caloriestracker.system.controller.food;

import com.caloriestracker.system.dto.response.food.FoodResponse;
import com.caloriestracker.system.service.food.FoodService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getAllFoods() {

        return ResponseEntity.ok(
                foodService.getAllFoods()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFoodById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                foodService.getById(id)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<FoodResponse> getFoodByName(
            @RequestParam String name
    ) {

        return ResponseEntity.ok(
                foodService.getByName(name)
        );
    }
}