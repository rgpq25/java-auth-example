package com.renzo.auth_example.auth.handlers;

import com.renzo.auth_example.auth.dto.TokenPair;
import com.renzo.auth_example.auth.exceptions.OAuthEmailAlreadyRegisteredException;
import com.renzo.auth_example.auth.services.AuthCookieService;
import com.renzo.auth_example.auth.services.GoogleOAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final GoogleOAuthService googleOAuthService;
    private final AuthCookieService authCookieService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(
            GoogleOAuthService googleOAuthService,
            AuthCookieService authCookieService
    ) {
        this.googleOAuthService = googleOAuthService;
        this.authCookieService = authCookieService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        try {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            TokenPair tokens = googleOAuthService.loginOrRegister(oauthUser);

            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    authCookieService.buildRefreshTokenCookie(tokens.refreshToken()).toString()
            );
            response.sendRedirect(frontendUrl + "/profile");
        } catch (OAuthEmailAlreadyRegisteredException exception) {
            response.sendRedirect(buildLoginErrorUrl("email_already_registered"));
        } catch (RuntimeException exception) {
            response.sendRedirect(buildLoginErrorUrl("google_auth_failed"));
        }
    }

    private String buildLoginErrorUrl(String errorCode) {
        return UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/login")
                .queryParam("oauthError", errorCode)
                .build()
                .toUriString();
    }
}
