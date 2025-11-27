package com.renzo.auth_example.config;

import com.renzo.auth_example.auth.models.Token;
import com.renzo.auth_example.auth.repositories.TokenRepository;
import com.renzo.auth_example.auth.services.TokenService;
import jakarta.servlet.http.Cookie;
import org.apache.coyote.BadRequestException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CookieValue;

import java.util.Arrays;
import java.util.Optional;


@Configuration
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;
    private final TokenRepository tokenRepository;

    public SecurityConfig (AuthenticationProvider authenticationProvider, JwtAuthFilter jwtAuthFilter, TokenRepository tokenRepository) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthFilter = jwtAuthFilter;
        this.tokenRepository = tokenRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/auth/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/auth/logout")
                                .addLogoutHandler((request, response, authentication) -> {
                                    Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                                            .filter(c -> c.getName().equals("refreshToken"))
                                            .findFirst();
                                    if (refreshTokenCookie.isEmpty()) throw new IllegalArgumentException("Invalid Refresh Token");
                                    logout(refreshTokenCookie.get().getValue());
                                })
                                .logoutSuccessHandler((request, response, authentication) ->
                                        SecurityContextHolder.clearContext())
                )
                .build();
    }

    private void logout (String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
        Token foundToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Refresh Token"));
        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRepository.save(foundToken);
    }
}
