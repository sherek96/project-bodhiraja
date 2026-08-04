package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.dto.StudentAdmissionContext;
import com.pirivena_project.pirivena.dto.StudentAdmissionRequest;
import com.pirivena_project.pirivena.dto.StudentAdmissionResponse;
import com.pirivena_project.pirivena.service.StudentAdmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student-admissions")
@RequiredArgsConstructor
public class StudentAdmissionController {

    private final StudentAdmissionService admissionService;

    @GetMapping("/context")
    public ResponseEntity<StudentAdmissionContext> getContext() {
        return ResponseEntity.ok(admissionService.getContext());
    }

    @PostMapping
    public ResponseEntity<StudentAdmissionResponse> admit(@RequestBody StudentAdmissionRequest request) {
        return new ResponseEntity<>(admissionService.admit(request), HttpStatus.CREATED);
    }
}
