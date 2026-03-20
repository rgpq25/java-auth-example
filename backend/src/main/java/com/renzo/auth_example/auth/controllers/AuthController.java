package com.renzo.auth_example.auth.controllers;

import com.renzo.auth_example.auth.dto.*;
import com.renzo.auth_example.auth.services.AuthCookieService;
import com.renzo.auth_example.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthCookieService authCookieService;

    public AuthController(AuthService authService, AuthCookieService authCookieService) {
        this.authService = authService;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<AccessTokenResponse> register(
            @Valid @RequestBody UserRegisterRequest request
    ) {
        TokenPair tokens = authService.register(request);
        ResponseCookie refreshCookie = authCookieService.buildRefreshTokenCookie(tokens.refreshToken());

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
        ResponseCookie refreshCookie = authCookieService.buildRefreshTokenCookie(tokens.refreshToken());

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
}
