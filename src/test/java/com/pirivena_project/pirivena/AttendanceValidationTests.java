// Tests backend rules directly, without using the frontend.
package com.pirivena_project.pirivena;

// Purpose: Tests the attendance validation behavior without using the frontend.

import com.pirivena_project.pirivena.model.*;
import com.pirivena_project.pirivena.enums.*;
import com.pirivena_project.pirivena.repository.AttendanceRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.service.AcademicYearLifecycleGuard;
import com.pirivena_project.pirivena.service.AttendanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceValidationTests {

    @Mock private AttendanceRepository attendanceRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private AcademicYearLifecycleGuard lifecycleGuard;
    @InjectMocks private AttendanceService service;

    @Test
    void rejectsFutureAttendance() {
        Attendance record = record(1, LocalDate.now().plusDays(1));
        assertThrows(IllegalArgumentException.class,
                () -> service.saveAttendanceSheet(10, List.of(record), false));
    }

    @Test
    void rejectsEnrollmentFromAnotherClassroom() {
        Attendance record = record(1, LocalDate.now());
        when(enrollmentRepository.findById(1)).thenReturn(Optional.of(enrollment(1, 99, true)));

        assertThrows(IllegalArgumentException.class,
                () -> service.saveAttendanceSheet(10, List.of(record), false));
    }

    @Test
    void rejectsInactiveEnrollment() {
        Attendance record = record(1, LocalDate.now());
        Enrollment enrollment = enrollment(1, 10, false);
        when(enrollmentRepository.findById(1)).thenReturn(Optional.of(enrollment));

        assertThrows(IllegalStateException.class,
                () -> service.saveAttendanceSheet(10, List.of(record), false));
    }

    @Test
    void rejectsDateOutsideAcademicYear() {
        LocalDate date = LocalDate.now().minusDays(30);
        Attendance record = record(1, date);
        Enrollment enrollment = enrollment(1, 10, true);
        enrollment.getClassroom().getAcademicYear().setStartDate(LocalDate.now().minusDays(10));
        when(enrollmentRepository.findById(1)).thenReturn(Optional.of(enrollment));

        assertThrows(IllegalArgumentException.class,
                () -> service.saveAttendanceSheet(10, List.of(record), false));
    }

    @Test
    void olderExistingAttendanceRequiresExplicitConfirmation() {
        LocalDate date = LocalDate.now().minusDays(1);
        Enrollment enrollment = enrollment(1, 10, true);
        Attendance record = record(1, date);
        record.setId(7);
        Attendance existing = record(1, date);
        existing.setId(7);
        existing.setEnrollment(enrollment);
        when(enrollmentRepository.findById(1)).thenReturn(Optional.of(enrollment));
        when(attendanceRepository.findById(7)).thenReturn(Optional.of(existing));
        when(attendanceRepository.findByEnrollmentIdAndAttendanceDate(1, date))
                .thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class,
                () -> service.saveAttendanceSheet(10, List.of(record), false));
    }

    @Test
    void confirmedHistoricalEditAndPresentStatusAreSaved() {
        LocalDate date = LocalDate.now().minusDays(1);
        Enrollment enrollment = enrollment(1, 10, true);
        Attendance record = record(1, date);
        record.setId(7);
        record.setStatus(AttendanceStatus.PRESENT);
        Attendance existing = record(1, date);
        existing.setId(7);
        existing.setEnrollment(enrollment);
        when(enrollmentRepository.findById(1)).thenReturn(Optional.of(enrollment));
        when(attendanceRepository.findById(7)).thenReturn(Optional.of(existing));
        when(attendanceRepository.findByEnrollmentIdAndAttendanceDate(1, date))
                .thenReturn(Optional.of(existing));
        when(attendanceRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<Attendance> result =
                service.saveAttendanceSheet(10, List.of(record), true);

        assertEquals(AttendanceStatus.PRESENT, result.get(0).getStatus());
        record.setStatus(AttendanceStatus.ABSENT);
        record.setId(null);
        when(attendanceRepository.findByEnrollmentIdAndAttendanceDate(1, date))
                .thenReturn(Optional.empty());
        result = service.saveAttendanceSheet(10, List.of(record), false);
        assertEquals(AttendanceStatus.ABSENT, result.get(0).getStatus());
    }

    private Attendance record(Integer enrollmentId, LocalDate date) {
        Enrollment reference = new Enrollment();
        reference.setId(enrollmentId);
        Attendance record = new Attendance();
        record.setEnrollment(reference);
        record.setAttendanceDate(date);
        record.setStatus(AttendanceStatus.PRESENT);
        return record;
    }

    private Enrollment enrollment(Integer id, Integer classroomId, boolean active) {
        AcademicYear year = new AcademicYear();
        year.setId(1);
        year.setName("Current");
        year.setStatus(AcademicYearStatus.CURRENT);
        year.setStartDate(LocalDate.now().minusDays(100));
        year.setEndDate(LocalDate.now().plusDays(100));
        Classroom classroom = new Classroom();
        classroom.setId(classroomId);
        classroom.setAcademicYear(year);
        Enrollment enrollment = new Enrollment();
        enrollment.setId(id);
        enrollment.setClassroom(classroom);
        enrollment.setStatus(active ? EnrollmentStatus.ACTIVE : EnrollmentStatus.WITHDRAWN);
        return enrollment;
    }
}
