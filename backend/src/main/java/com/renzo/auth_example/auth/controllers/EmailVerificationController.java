package com.renzo.auth_example.auth.controllers;

import com.renzo.auth_example.auth.dto.VerifyEmailRequest;
import com.renzo.auth_example.auth.services.EmailVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/email")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/resend")
    public ResponseEntity<Void> resendVerificationEmail(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        emailVerificationService.sendVerificationEmail(principal.getUsername());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyUser(
            @Valid @RequestBody VerifyEmailRequest request,
            Authentication authentication
    ) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        emailVerificationService.verifyEmail(principal.getUsername(), request.code());

        return ResponseEntity.noContent().build();
    }
}
