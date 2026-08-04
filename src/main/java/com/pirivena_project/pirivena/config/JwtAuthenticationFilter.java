package com.pirivena_project.pirivena.config;

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

        // 1. If the header is missing or doesn't start with "Bearer ", step aside and pass the request down the line
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Strip away the "Bearer " prefix (7 characters long) to isolate the raw JWT string
        jwt = authHeader.substring(7);

        // 3. Decode the payload and read the username. Expired or malformed
        // tokens are normal authentication failures, not server errors.
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Your session has expired. Please log in again.");
            return;
        }

        // 4. Verify the username exists and that this request hasn't already been security-cleared
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Look up the user record in MySQL using our translation bridge
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 5. Run the cryptographic check: Has it expired? Does the data line up?
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Construct Spring's native internal passport containing user info and roles
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // These are your "ROLE_ADMIN", "ROLE_PRINCIPAL", etc.
                );

                // Add IP address or browser context detail records to the passport
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the passport directly into Spring's security context holder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Open the checkpoint gate and let the request continue to its destination controller
        filterChain.doFilter(request, response);
    }
}
