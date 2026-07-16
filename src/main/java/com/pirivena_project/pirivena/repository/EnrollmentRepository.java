package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    // Custom deep query to verify if a student is already registered anywhere in a given academic year
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.classroom.academicYear.id = :academicYearId")
    Optional<Enrollment> findByStudentIdAndAcademicYearId(
            @Param("studentId") Integer studentId,
            @Param("academicYearId") Integer academicYearId
    );

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.classroom.academicYear.id = :academicYearId AND e.isActive = true")
    Optional<Enrollment> findActiveByStudentIdAndAcademicYearId(
            @Param("studentId") Integer studentId,
            @Param("academicYearId") Integer academicYearId
    );

    // Fetches the registry list for a specific classroom shell (Crucial for loading class lists in React)
    List<Enrollment> findByClassroomId(Integer classroomId);
    List<Enrollment> findByStudentId(Integer studentId);

    // Checks if a student is already registered in a specific classroom container
    boolean existsByStudentIdAndClassroomId(Integer studentId, Integer classroomId);

    boolean existsByStudentIdAndClassroomIdAndIsActiveTrue(Integer studentId, Integer classroomId);
}
