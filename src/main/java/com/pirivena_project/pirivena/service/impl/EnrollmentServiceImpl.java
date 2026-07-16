package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.modal.Classroom;
import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.repository.StudentRepository;
import com.pirivena_project.pirivena.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public Enrollment enrollStudent(Enrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getStudent().getId() == null || enrollment.getClassroom() == null || enrollment.getClassroom().getId() == null) {
            throw new IllegalArgumentException("Student and classroom are required for an enrollment.");
        }
        // Step 1: Extract the destination classroom details to locate the academic year context
        Classroom targetClassroom = classroomRepository.findById(enrollment.getClassroom().getId())
                .orElseThrow(() -> new RuntimeException("Validation Error: The target classroom container does not exist."));

        Integer targetYearId = targetClassroom.getAcademicYear().getId();
        String targetYearName = targetClassroom.getAcademicYear().getName();
        Integer studentId = enrollment.getStudent().getId();

        // Step 2: Run the security guardrail lookup
        studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Validation Error: Student does not exist."));

        enrollmentRepository.findActiveByStudentIdAndAcademicYearId(studentId, targetYearId)
                .ifPresent(existingEnrollment -> {
                    // Prevent save if an existing enrollment matches this year, unless it's the exact same record update
                    if (!existingEnrollment.getId().equals(enrollment.getId())) {
                        throw new RuntimeException("Validation Error: This student is already actively enrolled in '" +
                                existingEnrollment.getClassroom().getName() + "' for the " + targetYearName + " academic year.");
                    }
                });

        // Re-attach the validated managed classroom instance to avoid transient state errors
        enrollment.setClassroom(targetClassroom);
        if (enrollment.getEnrollmentDate() == null) enrollment.setEnrollmentDate(LocalDate.now());
        if (enrollment.getIsActive() == null) enrollment.setIsActive(true);
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public List<Enrollment> getEnrollmentsByClassroom(Integer classroomId) {
        return enrollmentRepository.findByClassroomId(classroomId);
    }

    @Override
    @Transactional
    public void cancelEnrollment(Integer id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new RuntimeException("Data Deletion Error: Enrollment record ID " + id + " does not exist.");
        }
        enrollmentRepository.deleteById(id);
    }
}
