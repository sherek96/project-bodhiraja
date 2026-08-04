package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    // Custom deep query to verify if a student is already registered anywhere in a given academic year
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.classroom.academicYear.id = :academicYearId")
    Optional<Enrollment> findByStudentIdAndAcademicYearId(
            @Param("studentId") Integer studentId,
            @Param("academicYearId") Integer academicYearId
    );

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.classroom.academicYear.id = :academicYearId AND e.status = 'ACTIVE'")
    Optional<Enrollment> findActiveByStudentIdAndAcademicYearId(
            @Param("studentId") Integer studentId,
            @Param("academicYearId") Integer academicYearId
    );

    // Fetches the registry list for a specific classroom shell (Crucial for loading class lists in React)
    List<Enrollment> findByClassroomId(Integer classroomId);
    List<Enrollment> findByStudentId(Integer studentId);

    // Checks if a student is already registered in a specific classroom container
    boolean existsByStudentIdAndClassroomId(Integer studentId, Integer classroomId);

    boolean existsByStudentIdAndClassroomIdAndStatus(Integer studentId, Integer classroomId, EnrollmentStatus status);

    List<Enrollment> findByClassroomAcademicYearIdAndStatus(Integer academicYearId, EnrollmentStatus status);

    boolean existsByStudentIdAndStatusAndClassroomAcademicYearStartDateAfter(Integer studentId, EnrollmentStatus status, LocalDate startDate);

    long countByClassroomAcademicYearIdAndStatus(Integer academicYearId, EnrollmentStatus status);
    long countByClassroomIdAndStatus(Integer classroomId, EnrollmentStatus status);
}
