package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.ClassroomSubject;
import com.pirivena_project.pirivena.service.ClassroomSubjectService;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classroom-subjects")
@RequiredArgsConstructor
public class ClassroomSubjectController {

    private final ClassroomSubjectService classroomSubjectService;
    private final AssignmentSecurity assignmentSecurity;

    // 1. Allocate a Subject and a Teacher to a Classroom container
    @PostMapping
    public ResponseEntity<ClassroomSubject> saveClassroomSubject(@RequestBody ClassroomSubject classroomSubject) {
        ClassroomSubject savedAllocation = classroomSubjectService.saveClassroomSubject(classroomSubject);
        return new ResponseEntity<>(savedAllocation, HttpStatus.CREATED);
    }

    // 2. Fetch all historical workload allocations in the system
    @GetMapping
    public ResponseEntity<List<ClassroomSubject>> getAllClassroomSubjects(Authentication authentication) {
        List<ClassroomSubject> allocations = classroomSubjectService.getAllClassroomSubjects();
        return ResponseEntity.ok(assignmentSecurity.visibleClassroomSubjects(allocations, authentication));
    }

    // 3. Fetch all subject assignments for a single class (Vital for setting up student grade sheets)
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<ClassroomSubject>> getClassroomSubjectsByClassroom(@PathVariable Integer classroomId, Authentication authentication) {
        List<ClassroomSubject> allocations = classroomSubjectService.getClassroomSubjectsByClassroom(classroomId);
        return ResponseEntity.ok(assignmentSecurity.visibleClassroomSubjects(allocations, authentication));
    }

    // 4. Remove a subject allocation from a classroom matrix
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeClassroomSubject(@PathVariable Integer id) {
        classroomSubjectService.removeClassroomSubject(id);
        return ResponseEntity.ok("Success: Subject assignment has been cleanly stripped from the classroom matrix.");
    }
}
