package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.service.EnrollmentService;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final AssignmentSecurity assignmentSecurity;

    // 1. Process a student registration into a target classroom shell
    @PostMapping
    public ResponseEntity<Enrollment> enrollStudent(@RequestBody Enrollment enrollment) {
        Enrollment processEnrollment = enrollmentService.enrollStudent(enrollment);
        return new ResponseEntity<>(processEnrollment, HttpStatus.CREATED);
    }

    // 2. View every single enrollment history ledger in the system
    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments(Authentication authentication) {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(assignmentSecurity.visibleEnrollments(enrollments, authentication));
    }

    // 3. Fetch the student list for a specific class (Perfect for teacher dashboards or mark entry)
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByClassroom(@PathVariable Integer classroomId, Authentication authentication) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByClassroom(classroomId);
        return ResponseEntity.ok(assignmentSecurity.visibleEnrollments(enrollments, authentication));
    }

    // 4. Withdraw an active enrollment without deleting its history
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelEnrollment(@PathVariable Integer id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.ok("Success: Student enrollment has been marked as withdrawn.");
    }
}
