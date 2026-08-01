package com.pirivena_project.pirivena.dto;

import com.pirivena_project.pirivena.modal.User;
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
