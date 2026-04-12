package com.caloriestracker.system.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 2, max = 10,message = "First name should be between 2 and 10 characters")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 10,message = "Last name should be between 2 and 10 characters")
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 15,message = "Username should be between 3 and 15 characters")
    private String username;

    @Email
    @NotBlank(message = "Email Should not be blank")
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password length should be at least 6")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain upper, lower, number, special char and be at least 8 characters"
    )
    private String password;
}