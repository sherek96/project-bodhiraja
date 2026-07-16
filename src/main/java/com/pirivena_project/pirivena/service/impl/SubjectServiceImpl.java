package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.modal.Subject;
import com.pirivena_project.pirivena.repository.SubjectRepository;
import com.pirivena_project.pirivena.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public Subject saveSubject(Subject subject) {
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

        return subjectRepository.save(subject);
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject getSubjectById(Integer id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data Retrieval Error: Subject record not found for ID: " + id));
    }

    @Override
    @Transactional
    public void deleteSubject(Integer id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Data Deletion Error: Cannot delete target record. Subject ID " + id + " does not exist.");
        }
        subjectRepository.deleteById(id);
    }
}