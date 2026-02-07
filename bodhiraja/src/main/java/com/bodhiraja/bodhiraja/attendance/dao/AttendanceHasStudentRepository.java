package com.bodhiraja.bodhiraja.attendance.dao;

import com.bodhiraja.bodhiraja.attendance.AttendanceHasStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceHasStudentRepository extends JpaRepository<AttendanceHasStudent, Integer> {
    // 1. Get the list of students for a specific Attendance Sheet (ID)
    List<AttendanceHasStudent> findByAttendanceId(Integer attendanceId);

    // 2. Get the history of a specific Student (ID)
    List<AttendanceHasStudent> findByStudentId(Integer studentId);
}