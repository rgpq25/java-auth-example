package com.renzo.auth_example.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

public record UserCreateRequest(
        @NotNull(message = "Name is required.")
        @NotBlank(message = "Name can't be blank.")
        String name,

        @NotNull(message = "Lastname is required.")
        @NotBlank(message = "Lastname can't be blank.")
        String lastname,

        @NotNull(message = "Email is required.")
        @NotBlank(message = "Email can't be blank.")
        @Email(message = "Email is not a valid email.")
        String email,

        @NotNull(message = "Password is required.")
        @NotBlank(message = "Password can't be blank.")
        String password
) {
}
