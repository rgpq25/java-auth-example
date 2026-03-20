package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.dto.JwtToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AuthCookieService {
    private final Environment environment;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public AuthCookieService(Environment environment) {
        this.environment = environment;
    }

    public ResponseCookie buildRefreshTokenCookie(JwtToken refreshToken) {
        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from("refreshToken", refreshToken.token())
                .httpOnly(true)
                .path("/auth")
                .maxAge(refreshTokenExpiration);

        if (isProd) {
            builder
                    .secure(true)
                    .sameSite("None")
                    .domain("TODO PROD URL");
        } else {
            builder
                    .secure(false)
                    .sameSite("Lax");
        }

        return builder.build();
    }
}
