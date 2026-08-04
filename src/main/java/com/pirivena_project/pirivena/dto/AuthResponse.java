package com.pirivena_project.pirivena.dto;

// Purpose: Carries auth results from the backend to the frontend.

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
}
