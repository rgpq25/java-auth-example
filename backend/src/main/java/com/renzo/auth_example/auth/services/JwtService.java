package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public Optional<String> extractUsername(final String token) {
        final Claims jwtToken = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getSubject().describeConstable();
    }

    public String generateToken(final User user) {
        return buildToken(user, jwtExpiration);
    }

    public String generateRefreshToken(final User user) {
        return buildToken(user, refreshExpiration);
    }

    private String buildToken(final User user, final long expiration) {
        return Jwts.builder()
                .id(user.getUserId().toString())
                .claims(Map.of("name", user.getName()))
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }

    public boolean isTokenValid(String token, User user) {
        Optional<String> email = extractUsername(token);
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Invalid Token;");
        }
        return (email.get().equals(user.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(final String token) {
        final Claims jwtToken = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getExpiration();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
