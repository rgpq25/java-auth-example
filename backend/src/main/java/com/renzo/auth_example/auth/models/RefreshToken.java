package com.renzo.auth_example.auth.models;

import com.renzo.auth_example.common.BaseEntity;
import com.renzo.auth_example.user.models.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "refreshTokens")
public class RefreshToken extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, length = 1024)
    private String token;

    @Column
    private boolean revoked;

    @Column
    private Date expiresAt;

    public RefreshToken() { }

    public RefreshToken(String token, Date expiresAt, boolean revoked, User user) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
