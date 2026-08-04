package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.dto.AttendanceDashboardResponse;
import com.pirivena_project.pirivena.enums.ClassroomStatus;
import com.pirivena_project.pirivena.modal.Attendance;
import com.pirivena_project.pirivena.modal.Classroom;
import com.pirivena_project.pirivena.repository.AcademicYearRepository;
import com.pirivena_project.pirivena.repository.AttendanceRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AttendanceDashboardService {
    private final AcademicYearRepository academicYearRepository;
    private final ClassroomRepository classroomRepository;
    private final AttendanceRepository attendanceRepository;
    private final AssignmentSecurity assignmentSecurity;

    @Transactional(readOnly = true)
    public AttendanceDashboardResponse getSummary(
            LocalDate from, LocalDate to, Authentication authentication) {
        validateRange(from, to);
        var currentYear = academicYearRepository.findByStatus(com.pirivena_project.pirivena.enums.AcademicYearStatus.CURRENT);
        if (currentYear.isEmpty()) return empty(from, to);

        List<Classroom> classrooms = assignmentSecurity.visibleClassrooms(
                classroomRepository.findByAcademicYearId(currentYear.get().getId()).stream()
                        .filter(classroom -> classroom.getStatus() == ClassroomStatus.ACTIVE)
                        .toList(), authentication);
        if (classrooms.isEmpty()) return empty(from, to);

        List<Integer> classroomIds = classrooms.stream().map(Classroom::getId).toList();
        List<Attendance> classroomRecords = attendanceRepository.findDashboardRecords(classroomIds, from, to);
        List<Attendance> records = assignmentSecurity.visibleAttendance(classroomRecords, authentication);

        List<AttendanceDashboardResponse.DailySummary> days = new ArrayList<>();
        long weeklyPresent = 0;
        long weeklyAbsent = 0;
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            LocalDate day = date;
            long present = records.stream().filter(record -> day.equals(record.getAttendanceDate())
                    && record.getStatus() == com.pirivena_project.pirivena.enums.AttendanceStatus.PRESENT).count();
            long absent = records.stream().filter(record -> day.equals(record.getAttendanceDate())
                    && record.getStatus() == com.pirivena_project.pirivena.enums.AttendanceStatus.ABSENT).count();
            weeklyPresent += present;
            weeklyAbsent += absent;
            days.add(new AttendanceDashboardResponse.DailySummary(
                    day, present, absent, percentage(present, absent), present + absent > 0));
        }

        LocalDate today = LocalDate.now();
        long todayPresent = records.stream().filter(record -> today.equals(record.getAttendanceDate())
                && record.getStatus() == com.pirivena_project.pirivena.enums.AttendanceStatus.PRESENT).count();
        long todayAbsent = records.stream().filter(record -> today.equals(record.getAttendanceDate())
                && record.getStatus() == com.pirivena_project.pirivena.enums.AttendanceStatus.ABSENT).count();
        Set<Integer> submittedIds = new HashSet<>();
        classroomRecords.stream().filter(record -> today.equals(record.getAttendanceDate()))
                .forEach(record -> submittedIds.add(record.getEnrollment().getClassroom().getId()));
        List<String> missing = classrooms.stream()
                .filter(classroom -> !submittedIds.contains(classroom.getId()))
                .map(Classroom::getName).sorted().toList();

        return new AttendanceDashboardResponse(days, weeklyPresent, weeklyAbsent,
                percentage(weeklyPresent, weeklyAbsent), todayPresent, todayAbsent,
                percentage(todayPresent, todayAbsent), classrooms.size(), submittedIds.size(), missing);
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) throw new IllegalArgumentException("Attendance date range is required.");
        if (to.isBefore(from)) throw new IllegalArgumentException("Attendance end date cannot precede start date.");
        if (from.plusDays(31).isBefore(to)) throw new IllegalArgumentException("Attendance dashboard range cannot exceed 31 days.");
    }

    private int percentage(long present, long absent) {
        long total = present + absent;
        return total == 0 ? 0 : (int) Math.round(present * 100.0 / total);
    }

    private AttendanceDashboardResponse empty(LocalDate from, LocalDate to) {
        List<AttendanceDashboardResponse.DailySummary> days = new ArrayList<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1))
            days.add(new AttendanceDashboardResponse.DailySummary(date, 0, 0, 0, false));
        return new AttendanceDashboardResponse(days, 0, 0, 0, 0, 0, 0, 0, 0, List.of());
    }
}
