package com.renzo.auth_example.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        @NotBlank(message = "Name can't be blank.")
        String name,

        @NotBlank(message = "Lastname can't be blank.")
        String lastname,

        @NotBlank(message = "Email can't be blank.")
        @Email(message = "Email is not a valid email.")
        String email,

        @NotBlank(message = "Password can't be blank.")
        String password
) {
}
