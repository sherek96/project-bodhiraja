package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.dto.StudentAdmissionContext;
import com.pirivena_project.pirivena.dto.StudentAdmissionRequest;
import com.pirivena_project.pirivena.dto.StudentAdmissionResponse;
import com.pirivena_project.pirivena.enums.GuardianStatus;
import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.enums.AcademicYearStatus;
import com.pirivena_project.pirivena.enums.EnrollmentStatus;
import com.pirivena_project.pirivena.modal.Classroom;
import com.pirivena_project.pirivena.enums.ClassroomStatus;
import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.modal.Role;
import com.pirivena_project.pirivena.modal.Student;
import com.pirivena_project.pirivena.modal.User;
import com.pirivena_project.pirivena.repository.AcademicYearRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.repository.GuardianRepository;
import com.pirivena_project.pirivena.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class StudentAdmissionService {

    private final AcademicYearRepository academicYearRepository;
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GuardianRepository guardianRepository;
    private final RoleRepository roleRepository;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public StudentAdmissionContext getContext() {
        AcademicYear year = requireCurrentYear();
        var classrooms = classroomRepository.findByAcademicYearId(year.getId()).stream()
                .filter(classroom -> classroom.getStatus() == ClassroomStatus.ACTIVE)
                .map(classroom -> {
                    long enrolled = enrollmentRepository.countByClassroomIdAndStatus(classroom.getId(), EnrollmentStatus.ACTIVE);
                    return new StudentAdmissionContext.ClassroomOption(
                            classroom.getId(), classroom.getName(), classroom.getCapacity(), enrolled,
                            Math.max(0, classroom.getCapacity() - enrolled));
                })
                .toList();
        return new StudentAdmissionContext(
                year.getId(), year.getName(), year.getStartDate(), year.getEndDate(), classrooms);
    }

    @Transactional
    public StudentAdmissionResponse admit(StudentAdmissionRequest request) {
        if (request == null || request.getStudent() == null || request.getClassroomId() == null) {
            throw new IllegalArgumentException("Student details and a classroom are required for admission.");
        }
        AcademicYear currentYear = requireCurrentYear();
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("The selected classroom does not exist."));
        if (!classroom.getAcademicYear().getId().equals(currentYear.getId())
                || classroom.getStatus() != ClassroomStatus.ACTIVE) {
            throw new IllegalStateException("Students can only be admitted into an active classroom in the current academic year.");
        }
        long enrolled = enrollmentRepository.countByClassroomIdAndStatus(classroom.getId(), EnrollmentStatus.ACTIVE);
        if (enrolled >= classroom.getCapacity()) {
            throw new IllegalStateException("The selected classroom has reached its capacity of " + classroom.getCapacity() + " students.");
        }

        Student studentRequest = request.getStudent();
        if (studentRequest.getGuardian() == null || studentRequest.getGuardian().getId() == null) {
            throw new IllegalArgumentException("An active guardian is required.");
        }
        var guardian = guardianRepository.findById(studentRequest.getGuardian().getId())
                .orElseThrow(() -> new IllegalArgumentException("The selected guardian does not exist."));
        if (guardian.getStatus() != GuardianStatus.ACTIVE) {
            throw new IllegalStateException("The selected guardian is not active.");
        }
        studentRequest.setGuardian(guardian);
        validateAdmissionDate(studentRequest.getJoinDate(), currentYear);

        Student student = studentService.createStudent(studentRequest);
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setClassroom(classroom);
        enrollment.setEnrollmentDate(student.getJoinDate());
        Enrollment savedEnrollment = enrollmentService.enrollStudent(enrollment);

        String username = null;
        if (Boolean.TRUE.equals(request.getCreateUserAccount())) {
            if (request.getTemporaryPassword() == null || request.getTemporaryPassword().length() < 8) {
                throw new IllegalArgumentException("The temporary password must contain at least 8 characters.");
            }
            Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                    .orElseThrow(() -> new IllegalStateException("The student role is not configured."));
            User account = new User();
            username = request.getUsername() == null || request.getUsername().isBlank()
                    ? student.getAdmissionNo().toLowerCase()
                    : request.getUsername().trim().toLowerCase();
            account.setUsername(username);
            account.setPassword(request.getTemporaryPassword());
            account.setStudent(student);
            account.setRoles(new HashSet<>(java.util.List.of(studentRole)));
            userService.createUser(account);
        }
        return new StudentAdmissionResponse(student, savedEnrollment, username);
    }

    private AcademicYear requireCurrentYear() {
        AcademicYear year = academicYearRepository.findByStatus(AcademicYearStatus.CURRENT)
                .orElseThrow(() -> new IllegalStateException(
                        "A current academic year is required before students can be registered and enrolled."));
        if (year.getStatus() != AcademicYearStatus.CURRENT) {
            throw new IllegalStateException("The active academic year is not in the Current state.");
        }
        return year;
    }

    private void validateAdmissionDate(LocalDate admissionDate, AcademicYear year) {
        if (admissionDate == null || admissionDate.isBefore(year.getStartDate())
                || admissionDate.isAfter(year.getEndDate())) {
            throw new IllegalArgumentException(
                    "Admission date must fall within the current academic year ("
                            + year.getStartDate() + " to " + year.getEndDate() + ").");
        }
    }
}
