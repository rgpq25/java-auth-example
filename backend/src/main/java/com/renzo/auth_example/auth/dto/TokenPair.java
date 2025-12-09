package com.renzo.auth_example.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenPair(
        JwtToken accessToken,
        JwtToken refreshToken
) {
}
