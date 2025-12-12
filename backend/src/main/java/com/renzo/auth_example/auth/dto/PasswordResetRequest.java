package com.renzo.auth_example.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordResetRequest(
        @NotNull(message = "Token is required.")
        @NotBlank(message = "Token can't be blank.")
        String token,

        @NotNull(message = "Email is required.")
        @NotBlank(message = "Email can't be blank.")
        @Email(message = "Email is not a valid email.")
        String email,

        @NotNull(message = "Password is required.")
        @NotBlank(message = "Password can't be blank.")
        String password
) {
}
