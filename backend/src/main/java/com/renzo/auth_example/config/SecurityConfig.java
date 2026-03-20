package com.renzo.auth_example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renzo.auth_example.auth.handlers.OAuth2AuthenticationFailureHandler;
import com.renzo.auth_example.auth.handlers.OAuth2AuthenticationSuccessHandler;
import com.renzo.auth_example.auth.models.RefreshToken;
import com.renzo.auth_example.auth.repositories.RefreshTokenRepository;
import com.renzo.auth_example.common.ErrorResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;

    public SecurityConfig (
            AuthenticationProvider authenticationProvider,
            JwtAuthFilter jwtAuthFilter,
            RefreshTokenRepository refreshTokenRepository,
            ObjectMapper objectMapper,
            OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler,
            OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthFilter = jwtAuthFilter;
        this.refreshTokenRepository = refreshTokenRepository;
        this.objectMapper = objectMapper;
        this.oauth2AuthenticationSuccessHandler = oauth2AuthenticationSuccessHandler;
        this.oauth2AuthenticationFailureHandler = oauth2AuthenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login-credentials",
                                "/auth/refresh",
                                "/auth/password/**",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .requestMatchers("/public/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oauth2AuthenticationSuccessHandler)
                        .failureHandler(oauth2AuthenticationFailureHandler)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        // Missing/invalid credentials → 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            ErrorResponse errorResponse = new ErrorResponse();
                            errorResponse.setMessage("Unauthorized");
                            errorResponse.setErrors(List.of("Authentication is required."));

                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            objectMapper.writeValue(response.getWriter(), errorResponse);
                        })
                        // Authenticated but not enough privileges → 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            ErrorResponse errorResponse = new ErrorResponse();
                            errorResponse.setMessage("Forbidden");
                            errorResponse.setErrors(List.of("You do not have permission."));

                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            objectMapper.writeValue(response.getWriter(), errorResponse);
                        })
                )
                .logout(logout ->
                        logout.logoutUrl("/auth/logout")
                                .addLogoutHandler((request, response, authentication) -> {
                                    Cookie[] cookies = request.getCookies();
                                    if (cookies == null) {
                                        throw new IllegalArgumentException("No cookies present");
                                    }

                                    Optional<Cookie> refreshTokenCookie = Arrays.stream(cookies)
                                            .filter(c -> c.getName().equals("refreshToken"))
                                            .findFirst();

                                    if (refreshTokenCookie.isEmpty()) {
                                        throw new IllegalArgumentException("Invalid Refresh Token");
                                    }

                                    logout(refreshTokenCookie.get().getValue());
                                })
                                .logoutSuccessHandler((request, response, authentication) ->
                                        SecurityContextHolder.clearContext())
                )
                .cors(cors -> {})
                .build();
    }

    private void logout (String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
        RefreshToken foundRefreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Refresh Token"));
        foundRefreshToken.setRevoked(true);
        refreshTokenRepository.save(foundRefreshToken);
    }
}
