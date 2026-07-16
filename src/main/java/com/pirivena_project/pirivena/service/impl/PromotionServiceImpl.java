package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.dto.PromotionRequestDTO;
import com.pirivena_project.pirivena.modal.Classroom;
import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.modal.Student;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.repository.StudentRepository;
import com.pirivena_project.pirivena.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional // Ensures an all-or-nothing rollback if any single student mapping fails
    public void promoteStudentRoster(PromotionRequestDTO promotionRequest) {
        if (promotionRequest == null || promotionRequest.getSourceClassroomId() == null || promotionRequest.getDestinationClassroomId() == null || promotionRequest.getStudentIds() == null || promotionRequest.getStudentIds().isEmpty()) {
            throw new IllegalArgumentException("Source classroom, destination classroom, and at least one student are required.");
        }
        if (promotionRequest.getSourceClassroomId().equals(promotionRequest.getDestinationClassroomId())) {
            throw new IllegalArgumentException("Source and destination classrooms must be different.");
        }
        if (new HashSet<>(promotionRequest.getStudentIds()).size() != promotionRequest.getStudentIds().size()) {
            throw new IllegalArgumentException("A student can only appear once in a promotion request.");
        }

        // 1. Fetch and verify both classroom containers
        Classroom sourceClass = classroomRepository.findById(promotionRequest.getSourceClassroomId())
                .orElseThrow(() -> new RuntimeException("Validation Error: Source classroom not found."));

        Classroom destClass = classroomRepository.findById(promotionRequest.getDestinationClassroomId())
                .orElseThrow(() -> new RuntimeException("Validation Error: Destination classroom not found."));

        // 2. Chronological Guardrail: Ensure target academic year is ahead of the source year
        LocalDate sourceStart = sourceClass.getAcademicYear().getStartDate();
        LocalDate destStart = destClass.getAcademicYear().getStartDate();

        if (!destStart.isAfter(sourceStart)) {
            throw new RuntimeException("Timeline Error: Destination classroom year (" + destClass.getAcademicYear().getName()
                    + ") must start after the source classroom year (" + sourceClass.getAcademicYear().getName() + ").");
        }

        Map<Integer, Enrollment> activeSourceEnrollments = enrollmentRepository.findByClassroomId(sourceClass.getId()).stream()
                .filter(enrollment -> Boolean.TRUE.equals(enrollment.getIsActive()))
                .collect(Collectors.toMap(enrollment -> enrollment.getStudent().getId(), Function.identity()));

        // 3. Roster Processing Loop
        for (Integer studentId : promotionRequest.getStudentIds()) {
            Enrollment sourceEnrollment = activeSourceEnrollments.get(studentId);
            if (sourceEnrollment == null) {
                throw new IllegalArgumentException("Student ID " + studentId + " is not actively enrolled in the source classroom.");
            }

            // Safety Guardrail: Prevent duplicate promotions into the same target classroom container
            if (enrollmentRepository.existsByStudentIdAndClassroomIdAndIsActiveTrue(studentId, destClass.getId())) {
                throw new RuntimeException("Data Conflict: Student ID " + studentId
                        + " is already enrolled in " + destClass.getName() + ". Promotion halted.");
            }

            // Fetch the student profile shell
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Data Error: Student ID " + studentId + " does not exist."));

            // Spawn a brand-new progressive enrollment ledger row
            Enrollment newEnrollment = new Enrollment();
            newEnrollment.setStudent(student);
            newEnrollment.setClassroom(destClass);
            newEnrollment.setEnrollmentDate(LocalDate.now()); // Marks their active promotion timestamp
            newEnrollment.setIsActive(true);

            enrollmentRepository.save(newEnrollment);
            sourceEnrollment.setIsActive(false);
            enrollmentRepository.save(sourceEnrollment);
        }
    }
}
