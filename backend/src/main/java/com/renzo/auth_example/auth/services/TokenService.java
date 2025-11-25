package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.models.Token;
import com.renzo.auth_example.auth.repositories.TokenRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void saveToken(Token token) {
        tokenRepository.save(token);
    }
}
