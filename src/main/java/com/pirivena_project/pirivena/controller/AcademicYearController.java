package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.service.AcademicYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    // 1. Save or Update an Academic Year (Triggers our safe transactional active-switch)
    @PostMapping
    public ResponseEntity<AcademicYear> createAcademicYear(@RequestBody AcademicYear academicYear) {
        AcademicYear savedYear = academicYearService.saveAcademicYear(academicYear);
        return new ResponseEntity<>(savedYear, HttpStatus.CREATED);
    }

    // 2. Fetch the entire historical list of Academic Years (For React drop-down select inputs)
    @GetMapping
    public ResponseEntity<List<AcademicYear>> getAllAcademicYears() {
        List<AcademicYear> years = academicYearService.getAllAcademicYears();
        return ResponseEntity.ok(years);
    }

    // 3. Fetch the single active timeline context (The master switch anchor for React initialization)
    @GetMapping("/active")
    public ResponseEntity<AcademicYear> getActiveAcademicYear() {
        AcademicYear activeYear = academicYearService.getActiveAcademicYear();
        return ResponseEntity.ok(activeYear);
    }
}
