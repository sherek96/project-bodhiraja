package com.bodhiraja.bodhiraja.attendance.controller;

import com.bodhiraja.bodhiraja.attendance.Attendance;
import com.bodhiraja.bodhiraja.attendance.AttendanceHasStudent;
import com.bodhiraja.bodhiraja.attendance.AttendanceStatus;
import com.bodhiraja.bodhiraja.attendance.dao.AttendanceHasStudentRepository;
import com.bodhiraja.bodhiraja.attendance.dao.AttendanceRepository;
import com.bodhiraja.bodhiraja.attendance.dao.AttendanceStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/attendance")
public class AttendanceController {

    // --- 1. Main Attendance Sheet (The Header) ---
    @Autowired
    private AttendanceRepository attendanceDao;

    @GetMapping(value = "/all", produces = "application/json")
    public List<Attendance> getAllAttendance() {
        return attendanceDao.findAll();
    }

    // --- 2. Attendance Status (Conducted/Cancelled) ---
    @Autowired
    private AttendanceStatusRepository attendanceStatusDao;

    @GetMapping(value = "/status/all", produces = "application/json")
    public List<AttendanceStatus> getAllStatuses() {
        return attendanceStatusDao.findAll();
    }

    // --- 3. Student Checkboxes (Present/Absent records) ---
    @Autowired
    private AttendanceHasStudentRepository attendanceHasStudentDao;

    @GetMapping(value = "/student/all", produces = "application/json")
    public List<AttendanceHasStudent> getAllStudentRecords() {
        return attendanceHasStudentDao.findAll();
    }
}