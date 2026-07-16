package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.modal.Attendance;
import com.pirivena_project.pirivena.repository.AttendanceRepository;
import com.pirivena_project.pirivena.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Override
    @Transactional // Forces atomic all-or-nothing execution across the entire batch array loop
    public List<Attendance> saveAttendanceSheet(List<Attendance> attendanceList) {
        List<Attendance> savedRecords = new ArrayList<>();

        for (Attendance record : attendanceList) {
            // Safety Validation Rule: Check for duplicate logs for this specific registration on this specific calendar date
            attendanceRepository.findByEnrollmentIdAndAttendanceDate(
                    record.getEnrollment().getId(),
                    record.getAttendanceDate()
            ).ifPresent(existingLog -> {
                // If an entry exists and it's a completely different row attempt, block it
                if (!existingLog.getId().equals(record.getId())) {
                    throw new RuntimeException("Validation Error: Daily attendance has already been logged for Student Enrollment ID "
                            + record.getEnrollment().getId() + " on " + record.getAttendanceDate());
                }
            });

            savedRecords.add(attendanceRepository.save(record));
        }

        return savedRecords;
    }

    @Override
    public List<Attendance> getAttendanceSheetByClassroomAndDate(Integer classroomId, LocalDate date) {
        return attendanceRepository.findByClassroomAndDate(classroomId, date);
    }

    @Override
    public List<Attendance> getStudentAttendance(Integer studentId) {
        return attendanceRepository.findByEnrollmentStudentIdOrderByAttendanceDateDesc(studentId);
    }
}
