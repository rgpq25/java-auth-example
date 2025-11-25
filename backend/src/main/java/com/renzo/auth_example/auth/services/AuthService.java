package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.dto.LoginRequest;
import com.renzo.auth_example.auth.dto.TokenResponse;
import com.renzo.auth_example.auth.dto.UserRegisterRequest;
import com.renzo.auth_example.auth.models.Token;
import com.renzo.auth_example.auth.repositories.TokenRepository;
import com.renzo.auth_example.user.User;
import com.renzo.auth_example.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public TokenResponse register(UserRegisterRequest request) {
        User user = userService.createUser(request);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, refreshToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse login(LoginRequest request) {
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
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Bearer Token");
        }

        final String refreshToken = authHeader.substring(7);
        final Optional<String> userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail.isEmpty()) {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        final User user =  userService.findByEmail(userEmail.get());

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        String accessToken = jwtService.generateToken(user);
        return new TokenResponse(accessToken, refreshToken);
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
