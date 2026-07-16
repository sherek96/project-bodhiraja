package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.modal.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {

    // Used to enforce that a classroom name is unique within a single academic year
    Optional<Classroom> findByNameAndAcademicYear(String name, AcademicYear academicYear);

    Optional<Classroom> findByClassTeacherIdAndAcademicYear(Integer classTeacherId, AcademicYear academicYear);

    // Used to fetch all classrooms belonging to a specific academic cycle
    List<Classroom> findByAcademicYearId(Integer academicYearId);
}
