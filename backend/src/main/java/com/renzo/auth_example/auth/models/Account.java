package com.renzo.auth_example.auth.models;

import com.renzo.auth_example.common.BaseEntity;
import com.renzo.auth_example.user.models.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {
    public enum ProviderType {
        CREDENTIALS,
        GOOGLE
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderType providerId;

    @Column(nullable = true)
    private String accessToken;

    @Column(nullable = true)
    private String refreshToken;

    @Column(nullable = true)
    private Date accessTokenExpiresAt;

    @Column(nullable = true)
    private Date refreshTokenExpiresAt;

    @Column(nullable = true)
    private String password;

    public Account() {}

    public Account(User user, String accountId, ProviderType providerId, String accessToken, String refreshToken, Date accessTokenExpiresAt, Date refreshTokenExpiresAt, String password) {
        this.user = user;
        this.accountId = accountId;
        this.providerId = providerId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public ProviderType getProviderId() {
        return providerId;
    }

    public void setProviderId(ProviderType providerId) {
        this.providerId = providerId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(Date accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public Date getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public void setRefreshTokenExpiresAt(Date refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
