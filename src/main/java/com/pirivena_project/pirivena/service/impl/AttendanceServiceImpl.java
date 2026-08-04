package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.modal.Attendance;
import com.pirivena_project.pirivena.enums.AttendanceStatus;
import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.enums.EnrollmentStatus;
import com.pirivena_project.pirivena.repository.AttendanceRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.service.AcademicYearLifecycleGuard;
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
    private final EnrollmentRepository enrollmentRepository;
    private final AcademicYearLifecycleGuard lifecycleGuard;

    @Override
    @Transactional
    public List<Attendance> saveAttendanceSheet(
            Integer classroomId,
            List<Attendance> attendanceList,
            boolean confirmHistoricalEdit) {
        if (classroomId == null) {
            throw new IllegalArgumentException("A classroom is required for the attendance sheet.");
        }
        if (attendanceList == null || attendanceList.isEmpty()) {
            throw new IllegalArgumentException("At least one attendance record is required.");
        }

        LocalDate sheetDate = attendanceList.get(0) == null
                ? null : attendanceList.get(0).getAttendanceDate();
        validateDate(sheetDate);
        List<Attendance> validatedRecords = new ArrayList<>();

        for (Attendance record : attendanceList) {
            if (record == null || record.getEnrollment() == null
                    || record.getEnrollment().getId() == null) {
                throw new IllegalArgumentException(
                        "Every attendance record must reference an enrollment.");
            }
            if (!sheetDate.equals(record.getAttendanceDate())) {
                throw new IllegalArgumentException(
                        "Every record in an attendance sheet must use the same date.");
            }

            Enrollment enrollment = enrollmentRepository.findById(record.getEnrollment().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Enrollment " + record.getEnrollment().getId() + " does not exist."));
            if (!classroomId.equals(enrollment.getClassroom().getId())) {
                throw new IllegalArgumentException(
                        "Enrollment " + enrollment.getId()
                                + " does not belong to the selected classroom.");
            }
            if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
                throw new IllegalStateException(
                        "Attendance cannot be recorded for inactive enrollment "
                                + enrollment.getId() + ".");
            }

            AcademicYear academicYear = enrollment.getClassroom().getAcademicYear();
            lifecycleGuard.requireOperational(academicYear, "Recording attendance");
            if (sheetDate.isBefore(academicYear.getStartDate())
                    || sheetDate.isAfter(academicYear.getEndDate())) {
                throw new IllegalArgumentException(
                        "Attendance date must fall within academic year "
                                + academicYear.getName() + " ("
                                + academicYear.getStartDate() + " to "
                                + academicYear.getEndDate() + ").");
            }

            Attendance existing = null;
            if (record.getId() != null) {
                existing = attendanceRepository.findById(record.getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Attendance record " + record.getId() + " does not exist."));
                if (!existing.getEnrollment().getId().equals(enrollment.getId())) {
                    throw new IllegalArgumentException(
                            "An attendance record cannot be transferred to another enrollment.");
                }
                if (!existing.getAttendanceDate().equals(sheetDate)) {
                    throw new IllegalArgumentException(
                            "The date of an existing attendance record cannot be changed.");
                }
            }

            var duplicate = attendanceRepository.findByEnrollmentIdAndAttendanceDate(
                    enrollment.getId(), sheetDate);
            if (duplicate.isPresent()
                    && (existing == null || !duplicate.get().getId().equals(existing.getId()))) {
                throw new IllegalStateException(
                        "Attendance already exists for enrollment "
                                + enrollment.getId() + " on " + sheetDate + ".");
            }
            if (existing != null && sheetDate.isBefore(LocalDate.now())
                    && !confirmHistoricalEdit) {
                throw new IllegalStateException(
                        "Editing older attendance requires explicit confirmation.");
            }

            AttendanceStatus status = record.getStatus();
            if (status == null) status = AttendanceStatus.PRESENT;
            record.setStatus(status);
            record.setEnrollment(enrollment);
            validatedRecords.add(record);
        }

        return attendanceRepository.saveAll(validatedRecords);
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Attendance date is required.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Attendance cannot be recorded for a future date.");
        }
    }

    @Override
    public List<Attendance> getAttendanceSheetByClassroomAndDate(
            Integer classroomId, LocalDate date) {
        return attendanceRepository.findByClassroomAndDate(classroomId, date);
    }

    @Override
    public List<Attendance> getStudentAttendance(Integer studentId) {
        return attendanceRepository.findByEnrollmentStudentIdOrderByAttendanceDateDesc(studentId);
    }
}
