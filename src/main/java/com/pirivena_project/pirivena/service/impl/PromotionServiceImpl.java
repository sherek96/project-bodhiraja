package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.dto.PromotionRequestDTO;
import com.pirivena_project.pirivena.enums.StudentStatus;
import com.pirivena_project.pirivena.modal.Attendance;
import com.pirivena_project.pirivena.modal.Classroom;
import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.enums.EnrollmentStatus;
import com.pirivena_project.pirivena.modal.ExamMark;
import com.pirivena_project.pirivena.modal.PromotionDecision;
import com.pirivena_project.pirivena.enums.PromotionOutcome;
import com.pirivena_project.pirivena.modal.Student;
import com.pirivena_project.pirivena.repository.AttendanceRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.repository.ExamMarkRepository;
import com.pirivena_project.pirivena.repository.PromotionDecisionRepository;
import com.pirivena_project.pirivena.repository.StudentRepository;
import com.pirivena_project.pirivena.repository.UserRepository;
import com.pirivena_project.pirivena.service.AcademicYearLifecycleGuard;
import com.pirivena_project.pirivena.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PromotionDecisionRepository decisionRepository;
    private final AttendanceRepository attendanceRepository;
    private final ExamMarkRepository examMarkRepository;
    private final UserRepository userRepository;
    private final AcademicYearLifecycleGuard lifecycleGuard;

    @Override
    @Transactional
    public List<PromotionDecision> recordDecisions(PromotionRequestDTO request, String processedBy) {
        validateRequest(request, processedBy);

        Classroom source = classroomRepository.findById(request.getSourceClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("Source classroom not found."));
        lifecycleGuard.requireOperational(source.getAcademicYear(), "Promotion decision");

        Classroom destination = resolveDestination(request, source);
        Map<Integer, Enrollment> sourceEnrollments = enrollmentRepository.findByClassroomId(source.getId()).stream()
                .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.ACTIVE)
                .collect(Collectors.toMap(enrollment -> enrollment.getStudent().getId(), Function.identity()));

        return request.getStudentIds().stream()
                .map(studentId -> recordStudentDecision(
                        request, processedBy.trim(), source, destination, sourceEnrollments, studentId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDecision> getStudentHistory(Integer studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found.");
        }
        return decisionRepository.findByStudentIdOrderByDecisionDateDescIdDesc(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDecision> getClassroomHistory(Integer classroomId) {
        if (!classroomRepository.existsById(classroomId)) {
            throw new IllegalArgumentException("Classroom not found.");
        }
        return decisionRepository.findBySourceClassroomIdOrderByDecisionDateDescIdDesc(classroomId);
    }

    private PromotionDecision recordStudentDecision(
            PromotionRequestDTO request,
            String processedBy,
            Classroom source,
            Classroom destination,
            Map<Integer, Enrollment> sourceEnrollments,
            Integer studentId) {
        Enrollment sourceEnrollment = sourceEnrollments.get(studentId);
        if (sourceEnrollment == null) {
            throw new IllegalArgumentException(
                    "Student ID " + studentId + " is not actively enrolled in the source classroom.");
        }
        preventDuplicateDecision(sourceEnrollment, request.getOutcome());

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student ID " + studentId + " does not exist."));

        if (destination != null) {
            createDestinationEnrollment(student, destination, request.getDecisionDate());
        }

        applyOutcome(request.getOutcome(), sourceEnrollment, student);

        List<Attendance> attendance = attendanceRepository.findByEnrollmentId(sourceEnrollment.getId());
        List<ExamMark> marks = examMarkRepository.findByEnrollmentId(sourceEnrollment.getId());

        PromotionDecision decision = new PromotionDecision();
        decision.setStudent(student);
        decision.setSourceEnrollment(sourceEnrollment);
        decision.setSourceClassroom(source);
        decision.setDestinationClassroom(destination);
        decision.setPreviousAcademicYear(source.getAcademicYear());
        decision.setNextAcademicYear(destination == null ? null : destination.getAcademicYear());
        decision.setOutcome(request.getOutcome());
        decision.setDecisionDate(request.getDecisionDate());
        decision.setDecisionReason(request.getDecisionReason().trim());
        decision.setProcessedBy(processedBy);
        decision.setAttendanceRecordedDays(attendance.size());
        decision.setAttendancePercentage(calculateAttendancePercentage(attendance));
        decision.setExaminationSubjectCount((int) marks.stream()
                .map(mark -> mark.getSubject().getId()).distinct().count());
        decision.setExaminationAverage(calculateExaminationAverage(marks));
        decision.setRemarks(normalizeOptional(request.getRemarks()));
        return decisionRepository.save(decision);
    }

    private Classroom resolveDestination(PromotionRequestDTO request, Classroom source) {
        boolean requiresDestination = request.getOutcome() == PromotionOutcome.PROMOTED
                || request.getOutcome() == PromotionOutcome.REPEATING;
        if (!requiresDestination) {
            if (request.getDestinationClassroomId() != null) {
                throw new IllegalArgumentException(
                        request.getOutcome() + " decisions must not include a destination classroom.");
            }
            return null;
        }
        if (request.getDestinationClassroomId() == null) {
            throw new IllegalArgumentException("A destination classroom is required for this outcome.");
        }
        if (request.getSourceClassroomId().equals(request.getDestinationClassroomId())) {
            throw new IllegalArgumentException("Source and destination classrooms must be different.");
        }

        Classroom destination = classroomRepository.findById(request.getDestinationClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("Destination classroom not found."));
        lifecycleGuard.requirePromotion(source.getAcademicYear(), destination.getAcademicYear());
        if (!destination.getAcademicYear().getStartDate().isAfter(source.getAcademicYear().getStartDate())) {
            throw new IllegalArgumentException("The destination academic year must start after the source year.");
        }
        return destination;
    }

    private void createDestinationEnrollment(Student student, Classroom destination, LocalDate decisionDate) {
        if (enrollmentRepository.findActiveByStudentIdAndAcademicYearId(
                student.getId(), destination.getAcademicYear().getId()).isPresent()) {
            throw new IllegalStateException(
                    student.getFullName() + " already has an active enrollment in the destination academic year.");
        }

        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudent(student);
        newEnrollment.setClassroom(destination);
        newEnrollment.setEnrollmentDate(decisionDate);
        newEnrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(newEnrollment);
    }

    private void applyOutcome(PromotionOutcome outcome, Enrollment sourceEnrollment, Student student) {
        if (outcome == PromotionOutcome.PENDING_DECISION) {
            return;
        }

        sourceEnrollment.setStatus(outcome == PromotionOutcome.PROMOTED
                ? EnrollmentStatus.PROMOTED
                : EnrollmentStatus.COMPLETED);
        enrollmentRepository.save(sourceEnrollment);

        if (outcome == PromotionOutcome.GRADUATED) {
            student.setStatus(StudentStatus.GRADUATED);
            deactivateStudentAccount(student);
        } else if (outcome == PromotionOutcome.DROPPED_OUT) {
            student.setStatus(StudentStatus.DROPPED_OUT);
            deactivateStudentAccount(student);
        }
        studentRepository.save(student);
    }

    private void deactivateStudentAccount(Student student) {
        userRepository.findByStudentId(student.getId()).ifPresent(user -> {
            user.setIsActive(false);
            userRepository.save(user);
        });
    }

    private void preventDuplicateDecision(Enrollment enrollment, PromotionOutcome outcome) {
        boolean finalDecisionExists =
                decisionRepository.existsBySourceEnrollmentIdAndOutcomeNot(
                        enrollment.getId(), PromotionOutcome.PENDING_DECISION);
        if (finalDecisionExists) {
            throw new IllegalStateException(
                    "A final promotion decision already exists for " + enrollment.getStudent().getFullName() + ".");
        }
        if (outcome == PromotionOutcome.PENDING_DECISION
                && decisionRepository.existsBySourceEnrollmentIdAndOutcome(
                        enrollment.getId(), PromotionOutcome.PENDING_DECISION)) {
            throw new IllegalStateException(
                    "A pending promotion decision already exists for " + enrollment.getStudent().getFullName() + ".");
        }
    }

    private BigDecimal calculateAttendancePercentage(List<Attendance> attendance) {
        if (attendance.isEmpty()) {
            return null;
        }
        long presentDays = attendance.stream().filter(record -> record.getStatus() == com.pirivena_project.pirivena.enums.AttendanceStatus.PRESENT).count();
        return BigDecimal.valueOf(presentDays)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(attendance.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateExaminationAverage(List<ExamMark> marks) {
        if (marks.isEmpty()) {
            return null;
        }
        BigDecimal total = marks.stream()
                .map(ExamMark::getMarksObtained)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(marks.size()), 2, RoundingMode.HALF_UP);
    }

    private void validateRequest(PromotionRequestDTO request, String processedBy) {
        if (request == null || request.getSourceClassroomId() == null
                || request.getStudentIds() == null || request.getStudentIds().isEmpty()
                || request.getOutcome() == null || request.getDecisionDate() == null) {
            throw new IllegalArgumentException(
                    "Source classroom, students, outcome, and decision date are required.");
        }
        if (request.getDecisionDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Decision date cannot be in the future.");
        }
        if (request.getDecisionReason() == null || request.getDecisionReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Decision reason is required.");
        }
        if (request.getDecisionReason().trim().length() > 500) {
            throw new IllegalArgumentException("Decision reason cannot exceed 500 characters.");
        }
        if (request.getRemarks() != null && request.getRemarks().trim().length() > 1000) {
            throw new IllegalArgumentException("Remarks cannot exceed 1000 characters.");
        }
        if (processedBy == null || processedBy.trim().isEmpty() || processedBy.trim().length() > 100) {
            throw new IllegalArgumentException("A valid decision processor is required.");
        }
        if (request.getStudentIds().stream().anyMatch(id -> id == null)
                || new HashSet<>(request.getStudentIds()).size() != request.getStudentIds().size()) {
            throw new IllegalArgumentException("Every selected student must be unique and valid.");
        }
    }

    private String normalizeOptional(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
