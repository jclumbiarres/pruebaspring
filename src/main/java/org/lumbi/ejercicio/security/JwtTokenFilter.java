package org.lumbi.ejercicio.security;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected boolean shouldNotFilter(@SuppressWarnings("null") HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/user/") ||
                path.startsWith("/h2-console") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = extractAuthorizationHeader(request);
        final String jwt = extractTokenFromHeader(authHeader);
        final String username = extractUsernameFromToken(jwt);

        authenticateUserIfValid(request, jwt, username);
        filterChain.doFilter(request, response);
    }

    private String extractAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    private String extractTokenFromHeader(String authHeader) {
        return Optional.ofNullable(authHeader)
                .filter(header -> header.startsWith(jwtTokenProvider.getTokenPrefix() + " "))
                .map(header -> header.substring(jwtTokenProvider.getTokenPrefix().length() + 1))
                .orElse(null);
    }

    private String extractUsernameFromToken(String token) {
        return Optional.ofNullable(token)
                .filter(jwtTokenProvider::validateToken)
                .map(jwtTokenProvider::getUsernameFromToken)
                .orElse(null);
    }

    private void authenticateUserIfValid(HttpServletRequest request, String token, String username) {
        switch (determineAuthenticationCase(token, username)) {
            case VALID_TOKEN -> {
                var authToken = new JwtAuthenticationToken(username, token);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            case INVALID_TOKEN -> SecurityContextHolder.clearContext();
            case NO_TOKEN -> {
            }
        }
    }

    private AuthenticationCase determineAuthenticationCase(String token, String username) {
        if (token == null) {
            return AuthenticationCase.NO_TOKEN;
        }
        return (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
                ? AuthenticationCase.VALID_TOKEN
                : AuthenticationCase.INVALID_TOKEN;
    }

    private enum AuthenticationCase {
        VALID_TOKEN, INVALID_TOKEN, NO_TOKEN
    }
}