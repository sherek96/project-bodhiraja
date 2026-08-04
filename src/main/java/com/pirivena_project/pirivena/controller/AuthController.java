package com.pirivena_project.pirivena.controller;

// Purpose: Exposes HTTP endpoints for auth operations.

import com.pirivena_project.pirivena.security.JwtUtil;
import com.pirivena_project.pirivena.security.CustomUserDetailsService;
import com.pirivena_project.pirivena.dto.LoginRequest;
import com.pirivena_project.pirivena.dto.AuthResponse;
import com.pirivena_project.pirivena.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Fetch the user details using our bridge service
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // 2. Check if the raw password matches the cryptographically hashed database password
            if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {

                // 3. If it matches, generate the digital passport (token)
                String token = jwtUtil.generateToken(userDetails);

                // 4. Return the token inside our custom response object
                return ResponseEntity.ok(new AuthResponse(token));
            } else {
                // Password didn't match
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (Exception e) {
            // User wasn't found in the database
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

}
