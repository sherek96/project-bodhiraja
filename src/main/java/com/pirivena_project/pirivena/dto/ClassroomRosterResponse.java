package com.pirivena_project.pirivena.dto;

import com.pirivena_project.pirivena.enums.StudentStatus;
import com.pirivena_project.pirivena.enums.StudentType;
import com.pirivena_project.pirivena.enums.EnrollmentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ClassroomRosterResponse(
        Integer classroomId,
        String classroomName,
        String academicYear,
        Integer capacity,
        long studentCount,
        List<SubjectSummary> subjects,
        List<StudentRosterItem> students
) {
    public record SubjectSummary(Integer id, String code, String name) {}

    public record StudentRosterItem(
            Integer enrollmentId,
            Integer studentId,
            String fullName,
            String admissionNo,
            StudentType studentType,
            StudentStatus studentStatus,
            Boolean enrollmentActive,
            EnrollmentStatus enrollmentStatus,
            LocalDate enrollmentDate,
            BigDecimal attendancePercentage,
            Integer latestTerm,
            BigDecimal recentExamAverage,
            List<SubjectSummary> subjects
    ) {}
}
