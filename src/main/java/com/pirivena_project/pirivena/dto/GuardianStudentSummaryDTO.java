package com.pirivena_project.pirivena.dto;

// Purpose: Carries the guardian student summary data needed by one API workflow.

import com.pirivena_project.pirivena.enums.GuardianRelationship;
import com.pirivena_project.pirivena.enums.StudentStatus;

public record GuardianStudentSummaryDTO(
        Integer id,
        String fullName,
        String admissionNo,
        StudentStatus status,
        GuardianRelationship relationship,
        String classroomName,
        String academicYearName
) {}
