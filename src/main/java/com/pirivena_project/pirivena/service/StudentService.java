package com.pirivena_project.pirivena.service;

// Purpose: Contains the business rules for student operations.

import com.pirivena_project.pirivena.enums.StudentStatus;
import com.pirivena_project.pirivena.model.Student;
import com.pirivena_project.pirivena.repository.StudentRepository;
import com.pirivena_project.pirivena.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.LocalDate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import com.pirivena_project.pirivena.enums.StudentType;
import com.pirivena_project.pirivena.enums.GuardianRelationship;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfilePictureStorageService profilePictureStorageService;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> searchStudents(String search, StudentStatus status, StudentType type,
                                        Integer guardianId, LocalDate admittedFrom, LocalDate admittedTo,
                                        String sortField, Sort.Direction direction) {
        Specification<Student> spec = (root, query, cb) -> cb.conjunction();
        if (search != null && !search.isBlank()) {
            String value = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("fullName")), value),
                    cb.like(cb.lower(root.get("ordinationName")), value),
                    cb.like(cb.lower(root.get("admissionNo")), value),
                    cb.like(cb.lower(root.join("guardian").get("fullName")), value)));
        }
        if (status != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        if (type != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("studentType"), type));
        if (guardianId != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("guardian").get("id"), guardianId));
        if (admittedFrom != null) spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("joinDate"), admittedFrom));
        if (admittedTo != null) spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("joinDate"), admittedTo));
        String safeSort = List.of("admissionNo", "fullName", "joinDate", "studentType", "status").contains(sortField) ? sortField : "admissionNo";
        return studentRepository.findAll(spec, Sort.by(direction, safeSort));
    }

    public Student getStudentById(Integer id) {
        return studentRepository.findById(id).orElseThrow(()-> new RuntimeException("Student not found with ID: " + id));
    }

    public Student createStudent(Student student) {
        validateStudent(student);
        // 1. Validation Rule: Monk status requires an ordination date
        if (student.getStudentType() != null && "MONK".equals(student.getStudentType().name())) {
            if (student.getDateOfOrdination() == null) {
                throw new RuntimeException("Monk should have an ordination date");
            }
        }

        // 2. Logic: Generate next sequential Admission Number (STUXXXX)
        String maxStudentNumber = studentRepository.findMaxAdmNo();
        String nextStudentNo = "STU0001";

        if (maxStudentNumber != null && maxStudentNumber.startsWith("STU")) {
            try {
                int numberPart = Integer.parseInt(maxStudentNumber.substring(3));
                nextStudentNo = String.format("STU%04d", numberPart + 1);
            } catch (NumberFormatException e) {
                nextStudentNo = "STU0001"; // Fallback if data string gets altered
            }
        }

        // 3. Set Auditing Defaults
        student.setAdmissionNo(nextStudentNo);
        student.setStatus(StudentStatus.ACTIVE);
        if (student.getGuardianRelationship() == null) student.setGuardianRelationship(GuardianRelationship.OTHER);

        // 4. Save and return
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Student student) {
        validateStudent(student);
        // 1. Fetch existing managed entity record (The Snapshot)
        Student existingStudent = studentRepository.findById(student.getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 2. Validation Rule: Monk status checking during updates
        if (student.getStudentType() != null && "MONK".equals(student.getStudentType().name())) {
            if (student.getDateOfOrdination() == null) {
                throw new RuntimeException("Monk should have an ordination date");
            }
        }

        // 3. Map update payloads explicitly onto the managed object
        existingStudent.setFullName(student.getFullName());
        existingStudent.setStatus(student.getStatus());
        existingStudent.setDateOfOrdination(student.getDateOfOrdination()); // Keeps it simple and updates null natively if sent
        existingStudent.setDob(student.getDob());
        existingStudent.setStudentType(student.getStudentType());
        existingStudent.setJoinDate(student.getJoinDate());
        existingStudent.setGuardian(student.getGuardian());
        existingStudent.setGuardianRelationship(student.getGuardianRelationship());
        existingStudent.setOrdinationName(student.getOrdinationName());
        existingStudent.setPreviousSchool(student.getPreviousSchool());

        // 4. Persist and return back
        Student savedStudent = studentRepository.save(existingStudent);
        userRepository.findByStudentId(savedStudent.getId()).ifPresent(user -> {
            user.setIsActive(savedStudent.getStatus() == StudentStatus.ACTIVE);
            userRepository.save(user);
        });
        return savedStudent;
    }

    @Transactional
    public Student deleteStudent(Integer id) {
        // 1. Fetch original record or throw error if missing
        Student studentToDelete = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student Not Found"));

        // 2. Perform Soft Delete state mutation
        studentToDelete.setStatus(StudentStatus.DROPPED_OUT);

        // 3. Persist record changes
        Student savedStudent = studentRepository.save(studentToDelete);
        userRepository.findByStudentId(id).ifPresent(user -> {
            user.setIsActive(false);
            userRepository.save(user);
        });
        return savedStudent;
    }

    public Student uploadProfilePicture(Integer id, MultipartFile file) {
        Student student = getStudentById(id);
        student.setProfilePicture(profilePictureStorageService.save(file, student.getProfilePicture(), "students"));
        return studentRepository.save(student);
    }

    private void validateStudent(Student student) {
        if (student.getFullName() == null || student.getFullName().trim().split("\\s+").length < 2) {
            throw new RuntimeException("Student full name must contain at least two words");
        }
        if (student.getDob() == null || !student.getDob().isBefore(LocalDate.now())) {
            throw new RuntimeException("Student date of birth must be in the past");
        }
        LocalDate today = LocalDate.now();
        if (student.getDob().plusYears(6).isAfter(today) || !student.getDob().plusYears(22).isAfter(today)) {
            throw new RuntimeException("Student must be between 6 and 21 years old");
        }
        if (student.getJoinDate() == null || student.getJoinDate().isAfter(today) || student.getJoinDate().isBefore(student.getDob())) {
            throw new RuntimeException("Student admission date must be after birth and cannot be in the future");
        }
        if (student.getGuardian() == null || student.getGuardian().getId() == null) {
            throw new RuntimeException("An active guardian must be selected");
        }
        if (student.getGuardianRelationship() == null) {
            throw new RuntimeException("The guardian relationship is required");
        }
        if (student.getPreviousSchool() == null || student.getPreviousSchool().isBlank()) {
            throw new RuntimeException("Previous school or Pirivena is required");
        }
        if (student.getStudentType() != null && "MONK".equals(student.getStudentType().name())) {
            if (student.getOrdinationName() == null || student.getOrdinationName().isBlank()) {
                throw new RuntimeException("Ordination name is required for monk students");
            }
            if (student.getDateOfOrdination() == null || student.getDateOfOrdination().isBefore(student.getDob().plusYears(6))
                    || student.getDateOfOrdination().isAfter(today)) {
                throw new RuntimeException("Ordination date must be between the student's sixth birthday and today");
            }
        } else {
            student.setOrdinationName(null);
            student.setDateOfOrdination(null);
        }
    }


}
