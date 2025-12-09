package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.dto.*;
import com.renzo.auth_example.auth.exceptions.InvalidVerificationCodeException;
import com.renzo.auth_example.auth.exceptions.VerificationExpiredException;
import com.renzo.auth_example.auth.models.RefreshToken;
import com.renzo.auth_example.auth.models.Verification;
import com.renzo.auth_example.mail.MailService;
import com.renzo.auth_example.mail.exceptions.MailSendingException;
import com.renzo.auth_example.user.exceptions.UserNotFoundException;
import com.renzo.auth_example.user.models.User;
import com.renzo.auth_example.user.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    private final UserService userService;
    private final AccountService accountService;
    private final VerificationService verificationService;
    private final JwtService jwtService;
    private final MailService mailService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authManager;

    public AuthService(
            UserService userService,
            AccountService accountService,
            VerificationService verificationService,
            JwtService jwtService,
            MailService mailService,
            RefreshTokenService refreshTokenService,
            AuthenticationManager authManager
    ) {
        this.userService = userService;
        this.accountService = accountService;
        this.verificationService = verificationService;
        this.jwtService = jwtService;
        this.mailService = mailService;
        this.refreshTokenService = refreshTokenService;
        this.authManager = authManager;
    }

    public TokenPair register(UserRegisterRequest request) {
        User user = userService.createUser(request);
        accountService.createCredentialsAccount(user, request.password());
        String verificationCode = verificationService.createEmailVerification(user.getEmail());

        try {
            mailService.sendEmailVerificationCode(user.getEmail(), verificationCode);
        } catch (MailSendingException ignored) {} // Exception is caught to continue registration. A user can later ask to be sent the email again.

        JwtToken accessToken = jwtService.generateAccessToken(user);
        JwtToken refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenService.createRefreshToken(user, refreshToken);

        return new TokenPair(accessToken, refreshToken);
    }

    public TokenPair loginCredentials(LoginCredentialsRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("email", request.email()));

        JwtToken accessToken = jwtService.generateAccessToken(user);
        JwtToken refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenService.createRefreshToken(user, refreshToken);

        return new TokenPair(accessToken, refreshToken);
    }

    public void verifyEmail(String email, String code) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));

        Verification pendingVerification = verificationService.getPendingVerification(email, Verification.VerificationType.EMAIL_VERIFICATION, code)
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid verification code."));

        if (pendingVerification.getExpiresAt().before(new Date())) {
            throw new VerificationExpiredException("Verification code has expired.");
        }

        user.setEmailVerified(true);
        userService.save(user);
        verificationService.delete(pendingVerification);
    }

    public void resendVerificationEmail(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));

        verificationService.deleteAllEmailVerifications(user.getEmail());
        String verificationCode = verificationService.createEmailVerification(user.getEmail());
        mailService.sendEmailVerificationCode(user.getEmail(), verificationCode);
    }

    public JwtToken refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new JwtException("Invalid refresh token"); // No refresh token present
        }

        Optional<RefreshToken> savedRefreshToken = refreshTokenService.getTokenByTokenString(refreshToken);
        if (savedRefreshToken.isEmpty() || savedRefreshToken.get().isRevoked()) {
            System.out.println("The token is non-existent / is revoked");
            throw new JwtException("Invalid refresh token"); // Refresh token doesnt exist / has been invalidated
        }

        String userEmail = jwtService.extractEmail(savedRefreshToken.get().getToken());
        if (userEmail == null || userEmail.isEmpty()) {
            System.out.println("No user found for the token");
            throw new JwtException("Invalid refresh token"); // When extracting claims from the token something went wrong
        }

        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("email", userEmail));
        if (!jwtService.isTokenValid(refreshToken, user.getEmail())) {
            System.out.println("Token is not valid");
            throw new JwtException("Invalid refresh token"); // Token is expired / doesn't belong to the user
        }

        return jwtService.generateAccessToken(user);
    }
}
