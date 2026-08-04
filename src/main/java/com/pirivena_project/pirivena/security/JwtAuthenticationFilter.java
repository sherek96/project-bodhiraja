package com.pirivena_project.pirivena.security;

// Purpose: Reads the login token from each request and authenticates the user.

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Checks the login token before a request reaches a protected controller.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Public requests may not contain a token, so continue without logging in a user.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove the "Bearer " prefix and keep only the token.
        jwt = authHeader.substring(7);

        // Invalid or expired tokens produce a normal 401 response.
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Your session has expired. Please log in again.");
            return;
        }

        // Authenticate only when another filter has not already done it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load the user and roles from the database.
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Check that the token belongs to this user and is not expired.
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Tell Spring which user is making this request.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Attach basic details about the current request.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Make the authenticated user available to permission checks.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue to the requested controller.
        filterChain.doFilter(request, response);
    }
}
