package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.ExamMark;
import com.pirivena_project.pirivena.service.ExamMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-marks")
@RequiredArgsConstructor
public class ExamMarkController {

    private final ExamMarkService examMarkService;
    private final AssignmentSecurity assignmentSecurity;

    // 1. Process or mass-update an entire classroom exam mark collection
    @PostMapping
    @PreAuthorize("@assignmentSecurity.markSheet(#examMarkList, authentication)")
    public ResponseEntity<List<ExamMark>> saveExamMarkSheet(@RequestBody List<ExamMark> examMarkList) {
        List<ExamMark> savedSheet = examMarkService.saveExamMarkSheet(examMarkList);
        return new ResponseEntity<>(savedSheet, HttpStatus.CREATED);
    }

    // 2. Fetch an entire grading sheet grid configuration for a classroom, subject, and term combo
    // Example lookup: /api/exam-marks/grid?classroomId=1&subjectId=1&termNumber=1
    @GetMapping("/grid")
    @PreAuthorize("@assignmentSecurity.subjectParticipant(#classroomId, #subjectId, authentication)")
    public ResponseEntity<List<ExamMark>> getGradingGrid(
            @RequestParam("classroomId") Integer classroomId,
            @RequestParam("subjectId") Integer subjectId,
            @RequestParam("termNumber") Integer termNumber,
            Authentication authentication) {

        List<ExamMark> grid = examMarkService.getGradingGrid(classroomId, subjectId, termNumber);
        return ResponseEntity.ok(assignmentSecurity.visibleMarks(grid, authentication));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("@assignmentSecurity.studentVisible(#studentId, authentication)")
    public ResponseEntity<List<ExamMark>> getStudentMarks(@PathVariable Integer studentId) {
        return ResponseEntity.ok(examMarkService.getStudentMarks(studentId));
    }
}
