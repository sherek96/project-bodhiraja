package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentRepository extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {
@Query("select max(s.admissionNo) from Student s")
    String findMaxAdmNo();
}
