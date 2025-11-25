package com.renzo.auth_example.config;

import com.renzo.auth_example.auth.models.Token;
import com.renzo.auth_example.auth.repositories.TokenRepository;
import com.renzo.auth_example.auth.services.JwtService;
import com.renzo.auth_example.auth.services.TokenService;
import com.renzo.auth_example.user.User;
import com.renzo.auth_example.user.UserRepository;
import com.renzo.auth_example.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService, TokenRepository tokenRepository, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().contains("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(7);
        Optional<String> userEmail = jwtService.extractUsername(jwtToken);
        if (userEmail.isEmpty() || SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        Token token = tokenRepository.findByToken(jwtToken)
                .orElse(null);
        if (token == null || token.isExpired() || token.isRevoked()) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail.get());
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
        if (user.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isTokenValid = jwtService.isTokenValid(jwtToken, user.get());
        if (!isTokenValid) {
            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
