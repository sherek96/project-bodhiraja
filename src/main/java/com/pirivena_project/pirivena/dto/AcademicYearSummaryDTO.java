package com.pirivena_project.pirivena.dto;

import com.pirivena_project.pirivena.enums.AcademicYearStatus;
import java.time.LocalDate;

public record AcademicYearSummaryDTO(
        Integer id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        AcademicYearStatus status,
        Boolean isCurrent,
        long classroomCount,
        long studentCount,
        long subjectCount,
        long unpromotedStudentCount
) {}
