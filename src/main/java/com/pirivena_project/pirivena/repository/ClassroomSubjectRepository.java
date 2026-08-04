package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes classroom subject records in the database.

import com.pirivena_project.pirivena.model.ClassroomSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClassroomSubjectRepository extends JpaRepository<ClassroomSubject, Integer> {

    // Guards against duplicating a subject within the same classroom environment
    Optional<ClassroomSubject> findByClassroomIdAndSubjectId(Integer classroomId, Integer subjectId);
    Optional<ClassroomSubject> findByClassroomIdAndSubjectIdAndIsActiveTrue(Integer classroomId, Integer subjectId);

    // Fetches the entire curriculum-teacher workload matrix for a specific classroom shell
    List<ClassroomSubject> findByClassroomId(Integer classroomId);

    List<ClassroomSubject> findByClassroomAcademicYearId(Integer academicYearId);
    List<ClassroomSubject> findByClassroomAcademicYearIdAndIsActiveTrue(Integer academicYearId);

    long countByClassroomAcademicYearId(Integer academicYearId);
    long countByClassroomAcademicYearIdAndIsActiveTrue(Integer academicYearId);
}
