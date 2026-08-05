package com.pirivena_project.pirivena.dto;

// Purpose: Carries the student admission data needed by one API workflow.

import java.time.LocalDate;
import java.util.List;

public record StudentAdmissionContext(
        Integer academicYearId,
        String academicYearName,
        LocalDate startDate,
        LocalDate endDate,
        List<ClassroomOption> classrooms) {

    public record ClassroomOption(
            Integer id,
            String name,
            Integer capacity,
            long enrolledStudents,
            long availablePlaces) {
    }
}
