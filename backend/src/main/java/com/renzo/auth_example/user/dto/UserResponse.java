package com.renzo.auth_example.user.dto;

public record UserResponse(
        Long id,
        String name,
        String email
) {
}
