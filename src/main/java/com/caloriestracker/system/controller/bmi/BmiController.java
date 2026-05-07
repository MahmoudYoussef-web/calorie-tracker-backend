package com.caloriestracker.system.controller.bmi;

import com.caloriestracker.system.dto.request.health.BmiRequest;
import com.caloriestracker.system.dto.response.health.BmiResponse;
import com.caloriestracker.system.dto.response.health.BmiStatusResponse;
import com.caloriestracker.system.service.health.bmi.BmiService;
import com.caloriestracker.system.util.AuthUtils;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bmi")
@RequiredArgsConstructor
public class BmiController {

    private final BmiService bmiService;
    private final AuthUtils authUtils;

    @PostMapping("/calculate")
    public ResponseEntity<BmiResponse> calculate(
            @Valid @RequestBody BmiRequest request
    ) {
        return ResponseEntity.ok(bmiService.calculate(request));
    }

    @GetMapping("/status")
    public ResponseEntity<BmiStatusResponse> getStatus(
            Authentication authentication
    ) {
        Long userId = authUtils.getUserId(authentication);
        return ResponseEntity.ok(bmiService.getStatusFromProfile(userId));
    }
}