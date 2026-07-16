package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    // Custom lookups to prevent silent database corruption from duplicate names or codes
    Optional<Subject> findByName(String name);
    Optional<Subject> findByCode(String code);
}