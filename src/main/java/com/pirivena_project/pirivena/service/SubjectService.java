package com.pirivena_project.pirivena.service;

// Purpose: Contains the business rules for subject operations.

import com.pirivena_project.pirivena.model.Subject;
import com.pirivena_project.pirivena.enums.SubjectStatus;
import com.pirivena_project.pirivena.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional
    public Subject saveSubject(Subject subject) {
        if (subject == null || subject.getName() == null || subject.getName().isBlank()
                || subject.getCode() == null || subject.getCode().isBlank()) {
            throw new IllegalArgumentException("Subject name and code are required.");
        }
        Subject existing = subject.getId() == null ? null : subjectRepository.findById(subject.getId())
                .orElseThrow(() -> new IllegalArgumentException("Subject record was not found."));
        if (existing != null && existing.getStatus() == SubjectStatus.ARCHIVED) {
            throw new IllegalStateException("Archived subjects are read-only.");
        }
        // Validation Rule: Enforce curriculum unique constraints before writing to the DB
        subjectRepository.findByName(subject.getName()).ifPresent(s -> {
            if (!s.getId().equals(subject.getId())) {
                throw new RuntimeException("Validation Error: A subject named '" + subject.getName() + "' already exists.");
            }
        });

        subjectRepository.findByCode(subject.getCode()).ifPresent(s -> {
            if (!s.getId().equals(subject.getId())) {
                throw new RuntimeException("Validation Error: A subject with code '" + subject.getCode() + "' already exists.");
            }
        });

        if (existing == null) {
            subject.setStatus(SubjectStatus.ACTIVE);
            return subjectRepository.save(subject);
        }
        existing.setName(subject.getName().trim());
        existing.setCode(subject.getCode().trim());
        if (subject.getStatus() != null && subject.getStatus() != SubjectStatus.ARCHIVED) {
            existing.setStatus(subject.getStatus());
        }
        return subjectRepository.save(existing);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Integer id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data Retrieval Error: Subject record not found for ID: " + id));
    }

    @Transactional
    public void deleteSubject(Integer id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Data Archival Error: Subject ID " + id + " does not exist."));
        subject.setStatus(SubjectStatus.ARCHIVED);
        subjectRepository.save(subject);
    }
}
