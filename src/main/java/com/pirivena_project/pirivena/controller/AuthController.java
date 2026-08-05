package com.pirivena_project.pirivena.controller;

// Purpose: Exposes HTTP endpoints for auth operations.

import com.pirivena_project.pirivena.security.JwtUtil;
import com.pirivena_project.pirivena.security.CustomUserDetailsService;
import com.pirivena_project.pirivena.dto.LoginRequest;
import com.pirivena_project.pirivena.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Fetch the user details using our bridge service
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // A correct password must never restore access to a deactivated account.
            if (!userDetails.isEnabled()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("This account is inactive. Contact an administrator.");
            }

            if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                String token = jwtUtil.generateToken(userDetails);

                return ResponseEntity.ok(new AuthResponse(token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

}
