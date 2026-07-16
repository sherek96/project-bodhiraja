package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.enums.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("api/enums")
public class EnumController {
    @GetMapping("/employee-statuses")
    public ResponseEntity<List<EmployeeStatus>> getEmployeeStatuses() {
        // Convert array to stream, filter out TERMINATED, and collect back to list
        List<EmployeeStatus> filteredStatuses = Arrays.stream(EmployeeStatus.values())
                .filter(status -> status != EmployeeStatus.TERMINATED)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredStatuses);
    }

    @GetMapping("/titles")
    public ResponseEntity<List<Title>> getTitles() {
        return ResponseEntity.ok(Arrays.asList(Title.values()));
    }

    @GetMapping("/teacher-grades")
    public ResponseEntity<List<TeacherGrade>> getTeacherGrades() {
        return ResponseEntity.ok(Arrays.asList(TeacherGrade.values()));
    }

    @GetMapping("/student-statuses")
    public ResponseEntity<List<StudentStatus>> getStudentStatuses() {
        return ResponseEntity.ok(Arrays.asList(StudentStatus.values()));
    }

    @GetMapping("/student-types")
    public ResponseEntity<List<StudentType>> getStudentTypes() {
        return ResponseEntity.ok(Arrays.asList(StudentType.values()));
    }

    @GetMapping("/guardian-statuses")
    public ResponseEntity<List<GuardianStatus>> getGuardianStatuses() {
        return ResponseEntity.ok(Arrays.asList(GuardianStatus.values()));
    }

    @GetMapping("/guardian-relationships")
    public ResponseEntity<List<GuardianRelationship>> getGuardianRelationships() {
        return ResponseEntity.ok(Arrays.asList(GuardianRelationship.values()));
    }

    @GetMapping("/genders")
    public ResponseEntity<List<Gender>> getGenders() {
        return ResponseEntity.ok(Arrays.asList(Gender.values()));
    }

}
