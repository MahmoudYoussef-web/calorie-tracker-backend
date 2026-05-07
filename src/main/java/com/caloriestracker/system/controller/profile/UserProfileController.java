package com.caloriestracker.system.controller.profile;

import com.caloriestracker.system.dto.request.profile.UserProfileRequest;
import com.caloriestracker.system.dto.response.profile.UserFullProfileResponse;
import com.caloriestracker.system.dto.response.profile.UserProfileResponse;
import com.caloriestracker.system.service.profile.UserProfileService;
import com.caloriestracker.system.util.AuthUtils;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final AuthUtils authUtils;

    @GetMapping
    public ResponseEntity<UserFullProfileResponse> getProfile(
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        UserFullProfileResponse response =
                userProfileService.getFullProfile(userId);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UserProfileRequest request,
            Authentication authentication
    ) {

        Long userId = authUtils.getUserId(authentication);

        UserProfileResponse response =
                userProfileService.updateProfile(userId, request);

        return ResponseEntity.ok(response);
    }
}