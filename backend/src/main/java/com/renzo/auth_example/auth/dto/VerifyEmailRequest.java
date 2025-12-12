package com.renzo.auth_example.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyEmailRequest(
        @NotNull(message = "Token is required.")
        @NotBlank(message = "Token can't be blank.")
        String token
) {
}
