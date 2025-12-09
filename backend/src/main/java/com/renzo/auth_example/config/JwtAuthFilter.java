package com.renzo.auth_example.config;

import com.renzo.auth_example.auth.services.JwtService;
import com.renzo.auth_example.user.models.User;
import com.renzo.auth_example.user.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthFilter(JwtService jwtService, UserService userService, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }


    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (
                request.getServletPath().startsWith("/public")
                        || request.getServletPath().startsWith("/error")
                        || request.getServletPath().startsWith("/auth/register")
                        || request.getServletPath().startsWith("/auth/login-credentials")
                        || request.getServletPath().startsWith("/auth/refresh")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authHeader.substring(7);
        String userEmail;
        try {
            userEmail = jwtService.extractEmail(accessToken);
        } catch (JwtException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
            return;
        }

        if (userEmail == null || userEmail.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = userService.findByEmail(userEmail)
                .orElse(null);
        if (user == null) { // TODO: If the user is emailVerified = false, cant do nothing
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            if (!jwtService.isTokenValid(accessToken, user.getEmail())) {
                throw new JwtException("Invalid token.");
            }
        } catch (JwtException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
            return;
        }

        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password("")
                .authorities(List.of())
                .build();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
