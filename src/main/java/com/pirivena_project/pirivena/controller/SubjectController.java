package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.Subject;
import com.pirivena_project.pirivena.service.SubjectService;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;
    private final AssignmentSecurity assignmentSecurity;

    // 1. Create or Update a Subject record
    @PostMapping
    public ResponseEntity<Subject> saveSubject(@RequestBody Subject subject) {
        Subject savedSubject = subjectService.saveSubject(subject);
        return new ResponseEntity<>(savedSubject, HttpStatus.CREATED);
    }

    // 2. Fetch the entire global curriculum master catalog
    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects(Authentication authentication) {
        List<Subject> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(assignmentSecurity.visibleSubjects(subjects, authentication));
    }

    // 3. Fetch a single subject profile by its Primary Key
    @GetMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.subjectVisible(#id, authentication)")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Integer id) {
        Subject subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    // 4. Delete a subject from the master inventory
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Integer id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Success: Subject record has been securely cleared from the curriculum repository.");
    }
}
