package com.renzo.auth_example.auth.services;

import com.renzo.auth_example.auth.dto.TokenPair;
import com.renzo.auth_example.auth.exceptions.OAuthEmailAlreadyRegisteredException;
import com.renzo.auth_example.auth.models.Account;
import com.renzo.auth_example.user.models.User;
import com.renzo.auth_example.user.services.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GoogleOAuthService {
    private final UserService userService;
    private final AccountService accountService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public GoogleOAuthService(
            UserService userService,
            AccountService accountService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userService = userService;
        this.accountService = accountService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public TokenPair loginOrRegister(OAuth2User oauthUser) {
        String googleAccountId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");
        Boolean emailVerified = oauthUser.getAttribute("email_verified");

        if (googleAccountId == null || googleAccountId.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google account is missing required attributes");
        }

        accountService.findByEmailAndProviderId(email, Account.ProviderType.CREDENTIALS)
                .ifPresent(account -> {
                    throw new OAuthEmailAlreadyRegisteredException(email);
                });

        Optional<Account> existingGoogleAccount = accountService.findByAccountIdAndProviderId(
                googleAccountId,
                Account.ProviderType.GOOGLE
        );

        User user;
        if (existingGoogleAccount.isPresent()) {
            user = existingGoogleAccount.get().getUser();
            user.setName(name != null && !name.isBlank() ? name : user.getName());
            user.setProfileImage(picture);
            user.setEmailVerified(Boolean.TRUE.equals(emailVerified));
            user = userService.updateUser(user);
        } else {
            user = userService.findByEmail(email)
                    .map(existingUser -> {
                        existingUser.setName(name != null && !name.isBlank() ? name : existingUser.getName());
                        existingUser.setProfileImage(picture);
                        existingUser.setEmailVerified(Boolean.TRUE.equals(emailVerified));
                        User updatedUser = userService.updateUser(existingUser);
                        accountService.createGoogleAccount(updatedUser, googleAccountId);
                        return updatedUser;
                    })
                    .orElseGet(() -> {
                        User createdUser = userService.createOAuthUser(
                                name != null && !name.isBlank() ? name : email,
                                email,
                                Boolean.TRUE.equals(emailVerified),
                                picture
                        );
                        accountService.createGoogleAccount(createdUser, googleAccountId);
                        return createdUser;
                    });
        }

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenService.createRefreshToken(user, refreshToken);

        return new TokenPair(accessToken, refreshToken);
    }
}
