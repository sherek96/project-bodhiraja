package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.Attendance;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    List<Attendance> saveAttendanceSheet(
            Integer classroomId,
            List<Attendance> attendanceList,
            boolean confirmHistoricalEdit);
    List<Attendance> getAttendanceSheetByClassroomAndDate(Integer classroomId, LocalDate date);
    List<Attendance> getStudentAttendance(Integer studentId);
}
