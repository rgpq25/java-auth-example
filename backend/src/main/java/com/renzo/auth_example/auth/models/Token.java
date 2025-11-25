package com.renzo.auth_example.auth.models;

import com.renzo.auth_example.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "tokens")
public class Token {
    public enum TokenType {
        BEARER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(unique = true)
    public String token;

    @Column
    public TokenType tokenType = TokenType.BEARER;

    @Column
    public boolean revoked;

    @Column
    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    public Token() { }

    public Token(String token, TokenType tokenType, boolean revoked, boolean expired, User user) {
        this.token = token;
        this.tokenType = tokenType;
        this.revoked = revoked;
        this.expired = expired;
        this.user = user;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
