package com.caloriestracker.system.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "This field should not be blank")
    private String identifier;

    @NotBlank
    @Size(min = 6, message = "Password length should be at least 6")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain upper, lower, number, special char and be at least 8 characters"
    )
    private String password;
}