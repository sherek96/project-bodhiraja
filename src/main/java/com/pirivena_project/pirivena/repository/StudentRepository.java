package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes student records in the database.

import com.pirivena_project.pirivena.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import com.pirivena_project.pirivena.enums.StudentStatus;

public interface StudentRepository extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {
@Query("select max(s.admissionNo) from Student s")
    String findMaxAdmNo();
    List<Student> findByGuardianIdOrderByFullNameAsc(Integer guardianId);
    List<Student> findByGuardianIdAndStatusOrderByFullNameAsc(Integer guardianId, StudentStatus status);
    long countByGuardianId(Integer guardianId);
}
