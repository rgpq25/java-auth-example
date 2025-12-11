package com.renzo.auth_example.auth.controllers;

import com.renzo.auth_example.auth.dto.*;
import com.renzo.auth_example.auth.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final Environment environment;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public AuthController(AuthService authService, Environment environment) {
        this.authService = authService;
        this.environment = environment;
    }

    @PostMapping("/register")
    public ResponseEntity<AccessTokenResponse> register(
            @Valid @RequestBody UserRegisterRequest request
    ) {
        TokenPair tokens = authService.register(request);
        ResponseCookie  refreshCookie = buildRefreshTokenCookie(tokens.refreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AccessTokenResponse(tokens.accessToken().token()));
    }

    @PostMapping("/login-credentials")
    public ResponseEntity<AccessTokenResponse> loginWithCredentials(
            @Valid @RequestBody LoginCredentialsRequest request
    ) {
        TokenPair tokens = authService.loginCredentials(request);
        ResponseCookie  refreshCookie = buildRefreshTokenCookie(tokens.refreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AccessTokenResponse(tokens.accessToken().token()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        JwtToken newAccessToken = authService.refreshAccessToken(refreshToken);

        return ResponseEntity
                .ok()
                .body(new AccessTokenResponse(newAccessToken.token()));
    }

    private ResponseCookie buildRefreshTokenCookie(JwtToken refreshToken) {
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
