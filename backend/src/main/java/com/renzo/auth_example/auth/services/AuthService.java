package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.dto.LoginRequest;
import com.renzo.auth_example.auth.dto.TokenPair;
import com.renzo.auth_example.auth.dto.TokenResponse;
import com.renzo.auth_example.auth.dto.UserRegisterRequest;
import com.renzo.auth_example.auth.models.Token;
import com.renzo.auth_example.user.User;
import com.renzo.auth_example.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final AuthenticationManager authManager;

    public AuthService(UserService userService, JwtService jwtService, TokenService tokenService, AuthenticationManager authManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.authManager = authManager;
    }

    public TokenPair register(UserRegisterRequest request) {
        User user = userService.createUser(request);

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, refreshToken);

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        return new TokenPair(jwtToken, refreshToken);
    }

    public TokenPair login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userService.findByEmail(request.email());
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, refreshToken);
        return new TokenPair(jwtToken, refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Invalid Refresh Token"); // No refresh token present
        }

        Optional<Token> savedRefreshToken = tokenService.getTokenByTokenString(refreshToken);
        if (savedRefreshToken.isEmpty() || savedRefreshToken.get().isRevoked()) {
            throw new IllegalArgumentException("Invalid Refresh Token"); // Refresh token doesnt exist / has been invalidated
        }

        String userEmail = jwtService.extractEmail(savedRefreshToken.get().getToken());
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("Invalid Refresh Token"); // When extracting claims from the token something went wrong
        }

        User user = userService.findByEmail(userEmail);
        if (!jwtService.isTokenValid(refreshToken, user.getEmail())) {
            throw new IllegalArgumentException("Invalid Refresh Token"); // Token is expired / doesn't belong to the user
        }

        return jwtService.generateToken(user);
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = new Token(
            jwtToken,
            Token.TokenType.BEARER,
            false,
            false,
            user
        );
        tokenService.saveToken(token);
    }
}
