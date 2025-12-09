package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.dto.JwtToken;
import com.renzo.auth_example.user.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.access-token.expiration}")
    private long accessTokenExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken generateAccessToken(final User user) {
        return buildToken(user, accessTokenExpiration);
    }

    public JwtToken generateRefreshToken(final User user) {
        return buildToken(user, refreshTokenExpiration);
    }

    private JwtToken buildToken(final User user, final long expiration) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("emailVerified", user.getEmailVerified());
        claims.put("profilePicture", user.getProfileImage());

        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(getSignKey())
                .compact();

        return new JwtToken(token, expiresAt);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String userEmail) {
        Claims claims = extractAllClaims(token);
        String email = claims.getSubject();
        Date expiration = claims.getExpiration();
        boolean notExpired = expiration != null && expiration.after(new Date());

        return email != null
                && email.equals(userEmail)
                && notExpired;
    }

    private Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
