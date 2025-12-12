package com.renzo.auth_example.auth.controllers;

import com.renzo.auth_example.auth.dto.PasswordRequestReset;
import com.renzo.auth_example.auth.dto.PasswordResetRequest;
import com.renzo.auth_example.auth.services.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/password")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request-reset")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordRequestReset request) {
        passwordResetService.sendPasswordResetEmail(request.email());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(request.token(), request.password());

        return ResponseEntity.noContent().build();
    }
}
