package com.pirivena_project.pirivena.dto;

// Purpose: Carries user page results from the backend to the frontend.

import com.pirivena_project.pirivena.model.User;
import java.util.List;
import java.util.Map;

public record UserPageResponse(
        List<User> content,
        long totalElements,
        int totalPages,
        int number,
        int size,
        Map<String, Long> summary
) {}
