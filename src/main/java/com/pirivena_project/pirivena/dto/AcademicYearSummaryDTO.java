package com.pirivena_project.pirivena.dto;

// Purpose: Carries the academic year summary data needed by one API workflow.

import com.pirivena_project.pirivena.enums.AcademicYearStatus;
import java.time.LocalDate;

public record AcademicYearSummaryDTO(
        Integer id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        AcademicYearStatus status,
        long classroomCount,
        long studentCount,
        long subjectCount,
        long unpromotedStudentCount
) {}
