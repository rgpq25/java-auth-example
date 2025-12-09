package com.renzo.auth_example.auth.dto;

import java.util.Date;

public record JwtToken(
        String token,
        Date expiresAt
) {
}
