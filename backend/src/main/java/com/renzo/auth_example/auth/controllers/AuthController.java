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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

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
            @Valid @RequestBody UserRegisterRequest request,
            HttpServletResponse response
    ) {
        TokenPair tokens = authService.register(request);
        attachRefreshToken(tokens.refreshToken(), response);

        AccessTokenResponse body = new AccessTokenResponse(tokens.accessToken().token());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/login-credentials")
    public ResponseEntity<AccessTokenResponse> loginWithCredentials(
            @Valid @RequestBody LoginCredentialsRequest request,
            HttpServletResponse response
    ) {
        TokenPair tokens = authService.loginCredentials(request);
        attachRefreshToken(tokens.refreshToken(), response);

        AccessTokenResponse body = new AccessTokenResponse(tokens.accessToken().token());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyEmailRequest request, Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        authService.verifyEmail(principal.getUsername(), request.code());
        return ResponseEntity.ok(Map.of("message", "Account verified successfully."));
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<?> resendVerificationEmail(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        authService.resendVerificationEmail(principal.getUsername());
        return ResponseEntity.ok(Map.of("message", "Verification email sent successfully."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        JwtToken newAccessToken = authService.refreshAccessToken(refreshToken);
        AccessTokenResponse body = new AccessTokenResponse(newAccessToken.token());
        return ResponseEntity.ok(body);
    }

    private void attachRefreshToken(JwtToken refreshToken, HttpServletResponse response) {
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

        ResponseCookie refreshCookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
