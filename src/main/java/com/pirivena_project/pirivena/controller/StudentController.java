package com.pirivena_project.pirivena.controller;

// Purpose: Exposes HTTP endpoints for student operations.

import com.pirivena_project.pirivena.model.Student;
import com.pirivena_project.pirivena.service.StudentService;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Sort;
import com.pirivena_project.pirivena.enums.StudentStatus;
import com.pirivena_project.pirivena.enums.StudentType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private AssignmentSecurity assignmentSecurity;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStudents(
            @RequestParam(defaultValue = "") String search, @RequestParam(required = false) StudentStatus status,
            @RequestParam(required = false) StudentType type, @RequestParam(required = false) Integer guardianId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate admittedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate admittedTo,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "admissionNo,asc") String sort, Authentication authentication) {
        List<Student> filtered = filteredVisible(search, status, type, guardianId, admittedFrom, admittedTo, sort, authentication);
        int safeSize = Math.max(1, Math.min(size, 100));
        int safePage = Math.max(page, 0);
        int from = Math.min(safePage * safeSize, filtered.size());
        int to = Math.min(from + safeSize, filtered.size());
        Map<String, Long> summary = Map.of(
                "active", filtered.stream().filter(s -> s.getStatus() == StudentStatus.ACTIVE).count(),
                "monk", filtered.stream().filter(s -> s.getStudentType() == StudentType.MONK).count(),
                "lay", filtered.stream().filter(s -> s.getStudentType() == StudentType.LAY).count());
        return ResponseEntity.ok(Map.of("content", filtered.subList(from, to), "totalElements", filtered.size(),
                "totalPages", (filtered.size() + safeSize - 1) / safeSize, "number", safePage, "size", safeSize,
                "summary", summary));
    }

    @GetMapping("/report")
    public ResponseEntity<List<Student>> getStudentReport(
            @RequestParam(defaultValue = "") String search, @RequestParam(required = false) StudentStatus status,
            @RequestParam(required = false) StudentType type, @RequestParam(required = false) Integer guardianId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate admittedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate admittedTo,
            @RequestParam(defaultValue = "admissionNo,asc") String sort, Authentication authentication) {
        return ResponseEntity.ok(filteredVisible(search, status, type, guardianId, admittedFrom, admittedTo, sort, authentication));
    }

    private List<Student> filteredVisible(String search, StudentStatus status, StudentType type, Integer guardianId,
                                          LocalDate from, LocalDate to, String sort, Authentication authentication) {
        String[] sortParts = sort.split(",", 2);
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return assignmentSecurity.visibleStudents(studentService.searchStudents(search, status, type, guardianId, from, to, sortParts[0], direction), authentication);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.studentVisible(#id, authentication)")
    public ResponseEntity<?> getStudentById(@PathVariable Integer id) {
        try {
            Student student = studentService.getStudentById(id);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while retrieving student: " + e.getMessage());
        }
    }
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            Student savedStudent = studentService.createStudent(student);
            return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Catches validation faults such as "Monk should have an ordination date"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error adding student record: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.studentVisible(#id, authentication)")
    public ResponseEntity<?> updateStudent(@PathVariable Integer id, @RequestBody Student student) {
        try {
            student.setId(id);
            Student updatedStudent = studentService.updateStudent(student);
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Catches "Student not found" or validation faults during updates
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error modifying student record: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.studentVisible(#id, authentication)")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer id) {
        try {
            Student deletedStudent = studentService.deleteStudent(id);
            return new ResponseEntity<>(deletedStudent, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error setting student deletion profile: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/profile-picture")
    @PreAuthorize("@assignmentSecurity.studentVisible(#id, authentication)")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(studentService.uploadProfilePicture(id, file));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
