package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.dto.ClassroomRosterResponse;
import com.pirivena_project.pirivena.modal.ExamMark;
import com.pirivena_project.pirivena.repository.AttendanceRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.repository.ClassroomSubjectRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.repository.ExamMarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomRosterService {
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ExamMarkRepository examMarkRepository;
    private final ClassroomSubjectRepository classroomSubjectRepository;

    @Transactional(readOnly = true)
    public ClassroomRosterResponse getRoster(Integer classroomId) {
        var classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found"));
        List<ClassroomRosterResponse.SubjectSummary> subjects = classroomSubjectRepository.findByClassroomId(classroomId).stream()
                .filter(allocation -> Boolean.TRUE.equals(allocation.getIsActive()))
                .map(allocation -> new ClassroomRosterResponse.SubjectSummary(allocation.getSubject().getId(),
                        allocation.getSubject().getCode(), allocation.getSubject().getName()))
                .sorted(Comparator.comparing(ClassroomRosterResponse.SubjectSummary::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
        var students = enrollmentRepository.findByClassroomId(classroomId).stream()
                .sorted(Comparator.comparing(enrollment -> enrollment.getStudent().getFullName(), String.CASE_INSENSITIVE_ORDER))
                .map(enrollment -> {
                    var attendance = attendanceRepository.findByEnrollmentId(enrollment.getId());
                    long present = attendance.stream().filter(item -> item.getStatus() == com.pirivena_project.pirivena.enums.AttendanceStatus.PRESENT).count();
                    BigDecimal attendancePercentage = attendance.isEmpty() ? null : BigDecimal.valueOf(present * 100.0 / attendance.size())
                            .setScale(1, RoundingMode.HALF_UP);
                    List<ExamMark> marks = examMarkRepository.findByEnrollmentId(enrollment.getId());
                    Integer latestTerm = marks.stream().map(ExamMark::getTermNumber).max(Integer::compareTo).orElse(null);
                    BigDecimal average = latestTerm == null ? null : marks.stream()
                            .filter(mark -> latestTerm.equals(mark.getTermNumber())).map(ExamMark::getMarksObtained)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(marks.stream().filter(mark -> latestTerm.equals(mark.getTermNumber())).count()), 1, RoundingMode.HALF_UP);
                    var student = enrollment.getStudent();
                    String displayName = student.getStudentType() == com.pirivena_project.pirivena.enums.StudentType.MONK
                            ? student.getOrdinationName() : student.getFullName();
                    if (displayName == null || displayName.isBlank()) displayName = "Ordination name not provided";
                    return new ClassroomRosterResponse.StudentRosterItem(enrollment.getId(), student.getId(), displayName,
                            student.getAdmissionNo(), student.getStudentType(), student.getStatus(), enrollment.getStatus() == com.pirivena_project.pirivena.enums.EnrollmentStatus.ACTIVE,
                            enrollment.getStatus(),
                            enrollment.getEnrollmentDate(), attendancePercentage, latestTerm, average, subjects);
                }).toList();
        long activeStudentCount = students.stream().filter(item -> Boolean.TRUE.equals(item.enrollmentActive())).count();
        int capacity = classroom.getCapacity() == null || classroom.getCapacity() < 1 ? 40 : classroom.getCapacity();
        return new ClassroomRosterResponse(classroom.getId(), classroom.getName(), classroom.getAcademicYear().getName(),
                capacity, activeStudentCount, subjects, students);
    }
}
