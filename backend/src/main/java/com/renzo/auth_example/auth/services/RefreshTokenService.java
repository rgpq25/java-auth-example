package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.dto.JwtToken;
import com.renzo.auth_example.auth.models.RefreshToken;
import com.renzo.auth_example.auth.repositories.RefreshTokenRepository;
import com.renzo.auth_example.user.models.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void createRefreshToken(User user, JwtToken token) {
        RefreshToken refreshToken = new RefreshToken(
                token.token(),
                token.expiresAt(),
                false,
                user
        );
        refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> getTokenByTokenString(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
