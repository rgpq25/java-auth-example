package com.renzo.auth_example.auth.controller;

import com.renzo.auth_example.auth.dto.LoginRequest;
import com.renzo.auth_example.auth.dto.TokenResponse;
import com.renzo.auth_example.auth.dto.UserRegisterRequest;
import com.renzo.auth_example.auth.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody UserRegisterRequest request) {
        TokenResponse token = authService.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return authService.refreshToken(authHeader);
    }
}
