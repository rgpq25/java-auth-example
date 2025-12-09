package com.renzo.auth_example.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyEmailRequest(
        @NotNull(message = "Code is required.")
        @NotBlank(message = "Code can't be blank.")
        String code
) {
}
