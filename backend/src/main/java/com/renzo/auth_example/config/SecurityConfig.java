package com.renzo.auth_example.config;

import com.renzo.auth_example.auth.models.Token;
import com.renzo.auth_example.auth.repositories.TokenRepository;
import com.renzo.auth_example.auth.services.TokenService;
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
                        req.requestMatchers("/auth/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/auth/logout")
                                .addLogoutHandler((request, response, authentication) -> {
                                    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                                    logout(authHeader);
                                })
                                .logoutSuccessHandler((request, response, authentication) ->
                                        SecurityContextHolder.clearContext())
                )
                .build();
    }

    private void logout (String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Token");
        }
        String refreshToken = token.substring(7);
        Token foundToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Token"));
        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRepository.save(foundToken);
    }
}
