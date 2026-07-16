package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.Attendance;
import com.pirivena_project.pirivena.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AssignmentSecurity assignmentSecurity;

    // 1. Save or bulk-update an entire daily classroom attendance registry sheet
    @PostMapping
    @PreAuthorize("@assignmentSecurity.attendanceSheet(#attendanceList, authentication)")
    public ResponseEntity<List<Attendance>> saveAttendanceSheet(@RequestBody List<Attendance> attendanceList) {
        List<Attendance> savedSheet = attendanceService.saveAttendanceSheet(attendanceList);
        return new ResponseEntity<>(savedSheet, HttpStatus.CREATED);
    }

    // 2. Fetch a specific day's records for a classroom (Perfect for editing an existing sheet)
    // Example path lookup: /api/attendances/classroom/1?date=2026-07-11
    @GetMapping("/classroom/{classroomId}")
    @PreAuthorize("@assignmentSecurity.classroomParticipant(#classroomId, authentication)")
    public ResponseEntity<List<Attendance>> getAttendanceByClassroomAndDate(
            @PathVariable Integer classroomId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {

        List<Attendance> sheet = attendanceService.getAttendanceSheetByClassroomAndDate(classroomId, date);
        return ResponseEntity.ok(assignmentSecurity.visibleAttendance(sheet, authentication));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("@assignmentSecurity.studentVisible(#studentId, authentication)")
    public ResponseEntity<List<Attendance>> getStudentAttendance(@PathVariable Integer studentId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(studentId));
    }
}
