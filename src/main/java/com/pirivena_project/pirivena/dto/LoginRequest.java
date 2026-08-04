package com.pirivena_project.pirivena.dto;

// Purpose: Carries login input from the frontend to the backend.

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
