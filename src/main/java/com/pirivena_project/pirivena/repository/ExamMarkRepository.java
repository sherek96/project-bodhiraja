package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes exam mark records in the database.

import com.pirivena_project.pirivena.model.ExamMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ExamMarkRepository extends JpaRepository<ExamMark, Integer> {

    // Unique guardrail lookup to prevent duplicate term entries for a student subject profile
    Optional<ExamMark> findByEnrollmentIdAndSubjectIdAndTermNumber(Integer enrollmentId, Integer subjectId, Integer termNumber);

    // Deep-fetch query to load a complete class mark sheet for a specific subject and term
    @Query("SELECT e FROM ExamMark e WHERE e.enrollment.classroom.id = :classroomId " +
            "AND e.subject.id = :subjectId AND e.termNumber = :termNumber")
    List<ExamMark> findGradingGrid(
            @Param("classroomId") Integer classroomId,
            @Param("subjectId") Integer subjectId,
            @Param("termNumber") Integer termNumber
    );

    // Add these two methods inside your existing ExamMarkRepository interface:

    // Pulls all grading logs for a specific student placement in a given term
    List<ExamMark> findByEnrollmentIdAndTermNumber(Integer enrollmentId, Integer termNumber);

    // Pulls the entire grading matrix for a classroom container to compute comparative rankings
    List<ExamMark> findByEnrollmentClassroomIdAndTermNumber(Integer classroomId, Integer termNumber);

    List<ExamMark> findByEnrollmentStudentIdOrderByTermNumberDesc(Integer studentId);
    List<ExamMark> findByEnrollmentId(Integer enrollmentId);
}
