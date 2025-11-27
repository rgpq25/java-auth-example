package com.renzo.auth_example.auth.controller;

import com.renzo.auth_example.auth.dto.LoginRequest;
import com.renzo.auth_example.auth.dto.TokenPair;
import com.renzo.auth_example.auth.dto.TokenResponse;
import com.renzo.auth_example.auth.dto.UserRegisterRequest;
import com.renzo.auth_example.auth.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final Environment environment;

    public AuthController(AuthService authService, Environment environment) {
        this.authService = authService;
        this.environment = environment;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(
            @Valid @RequestBody UserRegisterRequest request,
            HttpServletResponse response
    ) {
        TokenPair tokens = authService.register(request);
        attachRefreshToken(tokens.refreshToken(), response);
        TokenResponse body = new TokenResponse(tokens.accessToken());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        TokenPair tokens = authService.login(request);
        attachRefreshToken(tokens.refreshToken(), response);
        TokenResponse body = new TokenResponse(tokens.accessToken());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        TokenResponse body = new TokenResponse(newAccessToken);
        return ResponseEntity.ok(body);
    }

    private void attachRefreshToken(String refreshToken, HttpServletResponse response) {
        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/auth/refresh")
                .maxAge(7 * 24 * 60 * 60);

        if (isProd) {   // PROD: cross-site friendly
            builder
                    .secure(true)
                    .sameSite("None")
                    .domain("TODO PROD URL");
        } else {        // DEV: works over http://localhost
            builder
                    .secure(false)
                    .sameSite("Lax");
        }

        ResponseCookie refreshCookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
