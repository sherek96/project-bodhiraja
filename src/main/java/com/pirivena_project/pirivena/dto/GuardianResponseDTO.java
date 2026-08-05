package com.pirivena_project.pirivena.dto;

// Purpose: Carries the guardian response data needed by one API workflow.

import com.pirivena_project.pirivena.enums.*;
import java.time.*;

public record GuardianResponseDTO(
        Integer id, String fullName, Title title, String nic, LocalDate dob, Gender gender,
        String phonePrimary, String whatsappNumber, String email,
        GuardianStatus status, String address, String profilePicture,
        long linkedStudentCount, boolean sensitiveDataVisible,
        LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy
) {}
