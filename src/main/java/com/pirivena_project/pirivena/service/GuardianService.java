package com.pirivena_project.pirivena.service;

// Purpose: Contains the business rules for guardian operations.

import com.pirivena_project.pirivena.enums.GuardianStatus;
import com.pirivena_project.pirivena.model.Guardian;
import com.pirivena_project.pirivena.repository.GuardianRepository;
import com.pirivena_project.pirivena.repository.StudentRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.dto.GuardianStudentSummaryDTO;
import com.pirivena_project.pirivena.enums.GuardianRelationship;
import com.pirivena_project.pirivena.enums.StudentStatus;
import com.pirivena_project.pirivena.dto.GuardianDeactivationRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.time.DateTimeException;
import java.time.LocalDate;
import com.pirivena_project.pirivena.enums.Gender;
import com.pirivena_project.pirivena.dto.GuardianResponseDTO;
import com.pirivena_project.pirivena.model.GuardianAccessAudit;
import com.pirivena_project.pirivena.repository.GuardianAccessAuditRepository;
import java.time.LocalDateTime;
import java.util.Comparator;

@Service
public class GuardianService {

    @Autowired
    private GuardianRepository guardianRepository;
    @Autowired
    private ProfilePictureStorageService profilePictureStorageService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private GuardianAccessAuditRepository guardianAccessAuditRepository;

    public List<Guardian> getAllGuardians() {
        return guardianRepository.findAll();
    }

    public Guardian getGuardianById(Integer id) {
        return guardianRepository.findById(id).orElseThrow(() -> new RuntimeException("Guardian not found with ID: " + id));
    }

    public List<GuardianStudentSummaryDTO> getLinkedStudents(Integer guardianId) {
        getGuardianById(guardianId);
        return studentRepository.findByGuardianIdOrderByFullNameAsc(guardianId).stream().map(student -> {
            var activeEnrollment = enrollmentRepository.findByStudentId(student.getId()).stream()
                    .filter(enrollment -> enrollment.getStatus() == com.pirivena_project.pirivena.enums.EnrollmentStatus.ACTIVE).findFirst().orElse(null);
            String displayName = student.getStudentType() == com.pirivena_project.pirivena.enums.StudentType.MONK
                    ? student.getOrdinationName() : student.getFullName();
            if (displayName == null || displayName.isBlank()) displayName = "Ordination name not provided";
            return new GuardianStudentSummaryDTO(
                    student.getId(), displayName, student.getAdmissionNo(), student.getStatus(),
                    student.getGuardianRelationship(),
                    activeEnrollment == null ? null : activeEnrollment.getClassroom().getName(),
                    activeEnrollment == null ? null : activeEnrollment.getClassroom().getAcademicYear().getName());
        }).toList();
    }

    public Map<String, Long> getRelationshipSummary(List<Integer> guardianIds) {
        var students = guardianIds.stream().flatMap(id -> studentRepository.findByGuardianIdOrderByFullNameAsc(id).stream()).toList();
        long parents = students.stream().filter(student -> student.getGuardianRelationship() == GuardianRelationship.FATHER
                || student.getGuardianRelationship() == GuardianRelationship.MOTHER).count();
        return Map.of("parents", parents, "other", (long) students.size() - parents, "relationships", (long) students.size());
    }

    public List<Guardian> filterAndSortGuardians(List<Guardian> source, String search, GuardianStatus status, Gender gender,
                                                  GuardianRelationship relationship, Boolean multipleStudents,
                                                  Boolean missingContact, String sortField,
                                                  boolean descending) {
        String queryText = search == null ? "" : search.trim().toLowerCase();
        Comparator<Guardian> comparator = switch (sortField == null ? "fullName" : sortField) {
            case "nic" -> Comparator.comparing(g -> nullSafe(g.getNic()), String.CASE_INSENSITIVE_ORDER);
            case "phonePrimary" -> Comparator.comparing(g -> nullSafe(g.getPhonePrimary()));
            case "linkedStudentCount" -> Comparator.comparingLong(g -> studentRepository.countByGuardianId(g.getId()));
            case "status" -> Comparator.comparing(g -> g.getStatus().name());
            case "createdAt" -> Comparator.comparing(g -> g.getCreatedAt() == null ? LocalDateTime.MIN : g.getCreatedAt());
            default -> Comparator.comparing(g -> nullSafe(g.getFullName()), String.CASE_INSENSITIVE_ORDER);
        };
        if (descending) comparator = comparator.reversed();
        return source.stream()
                .filter(g -> queryText.isEmpty() || String.join(" ", nullSafe(g.getFullName()), nullSafe(g.getNic()), nullSafe(g.getPhonePrimary()),
                        nullSafe(g.getWhatsappNumber()), nullSafe(g.getEmail())).toLowerCase().contains(queryText))
                .filter(g -> status == null || g.getStatus() == status)
                .filter(g -> gender == null || g.getGender() == gender)
                .filter(g -> relationship == null || students(g).stream().anyMatch(s -> s.getGuardianRelationship() == relationship))
                .filter(g -> multipleStudents == null || (studentRepository.countByGuardianId(g.getId()) > 1) == multipleStudents)
                .filter(g -> missingContact == null || (isBlank(g.getWhatsappNumber()) == missingContact))
                .sorted(comparator).toList();
    }

    public GuardianResponseDTO toResponse(Guardian guardian, boolean revealSensitive) {
        return new GuardianResponseDTO(guardian.getId(), guardian.getFullName(), guardian.getTitle(),
                revealSensitive ? guardian.getNic() : mask(guardian.getNic()), revealSensitive ? guardian.getDob() : null,
                guardian.getGender(), revealSensitive ? guardian.getPhonePrimary() : mask(guardian.getPhonePrimary()),
                revealSensitive ? guardian.getWhatsappNumber() : mask(guardian.getWhatsappNumber()),
                revealSensitive ? guardian.getEmail() : maskEmail(guardian.getEmail()), guardian.getStatus(),
                revealSensitive ? guardian.getAddress() : "Restricted", guardian.getProfilePicture(),
                studentRepository.countByGuardianId(guardian.getId()), revealSensitive, guardian.getCreatedAt(), guardian.getUpdatedAt(),
                guardian.getCreatedBy(), guardian.getUpdatedBy());
    }

    public void auditAccess(Integer guardianId, String username, String action) {
        guardianAccessAuditRepository.save(new GuardianAccessAudit(null, guardianId, username, action, LocalDateTime.now()));
    }

    @Transactional
    public Guardian createGuardian(Guardian guardian) {
        normalizeGuardian(guardian);
        validateGuardian(guardian);
        // 1. Validation Rules (Unique checks)
        if (guardianRepository.existsByNic(guardian.getNic())) {
            throw new RuntimeException("NIC already exists");
        }
        if (guardianRepository.existsByPhonePrimary(guardian.getPhonePrimary())) {
            throw new RuntimeException("PHONE primary already exists");
        }

        // 2. Set Default Configuration values
        guardian.setStatus(GuardianStatus.ACTIVE);

        // 3. Save to database
        return guardianRepository.save(guardian);
    }

    @Transactional
    public Guardian updateGuardian(Guardian guardian) {
        normalizeGuardian(guardian);
        validateGuardian(guardian);
        // 1. Fetch existing managed record (The Snapshot)
        Guardian existingGuardian = guardianRepository.findById(guardian.getId())
                .orElseThrow(() -> new RuntimeException("Guardian not found"));
        if (existingGuardian.getStatus() == GuardianStatus.ACTIVE && guardian.getStatus() == GuardianStatus.INACTIVE) {
            throw new RuntimeException("Use the guardian deactivation workflow so linked active students can be protected");
        }

        // 2. Validate Duplicates (Excluding current record ID)
        if (guardianRepository.existsByNicAndIdNot(guardian.getNic(), guardian.getId())) {
            throw new RuntimeException("NIC already exists");
        }
        if (guardianRepository.existsByPhonePrimaryAndIdNot(guardian.getPhonePrimary(), guardian.getId())) {
            throw new RuntimeException("Phone number already exists");
        }

        // 3. Update fields on the managed object instance
        existingGuardian.setNic(guardian.getNic());
        existingGuardian.setPhonePrimary(guardian.getPhonePrimary());
        existingGuardian.setStatus(guardian.getStatus());
        existingGuardian.setAddress(guardian.getAddress());
        existingGuardian.setTitle(guardian.getTitle());
        existingGuardian.setFullName(guardian.getFullName());
        existingGuardian.setDob(guardian.getDob());
        existingGuardian.setGender(guardian.getGender());
        existingGuardian.setWhatsappNumber(guardian.getWhatsappNumber());
        existingGuardian.setEmail(guardian.getEmail());

        // 4. Save and return updated entity
        return guardianRepository.save(existingGuardian);
    }

    @Transactional
    public Guardian deactivateGuardian(Integer id, GuardianDeactivationRequest request) {
        // 1. Fetch original record or throw error if missing
        Guardian guardianToDelete = guardianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guardian Not Found"));

        var affectedStudents = studentRepository.findByGuardianIdAndStatusOrderByFullNameAsc(id, StudentStatus.ACTIVE);
        if (!affectedStudents.isEmpty()) {
            if (request == null || request.replacementGuardianId() == null || request.replacementRelationship() == null) {
                String names = affectedStudents.stream().map(student -> student.getFullName() + " (" + student.getAdmissionNo() + ")")
                        .collect(java.util.stream.Collectors.joining(", "));
                throw new IllegalStateException("Replacement guardian and relationship required for: " + names);
            }
            if (id.equals(request.replacementGuardianId())) {
                throw new IllegalArgumentException("Replacement guardian must be different from the guardian being deactivated");
            }
            Guardian replacement = guardianRepository.findById(request.replacementGuardianId())
                    .orElseThrow(() -> new IllegalArgumentException("Replacement guardian not found"));
            if (replacement.getStatus() != GuardianStatus.ACTIVE) {
                throw new IllegalArgumentException("Replacement guardian must be active");
            }
            affectedStudents.forEach(student -> {
                student.setGuardian(replacement);
                student.setGuardianRelationship(request.replacementRelationship());
            });
            studentRepository.saveAll(affectedStudents);
        }

        // 2. Set Soft Delete state to INACTIVE
        guardianToDelete.setStatus(GuardianStatus.INACTIVE);

        // 3. Persist change back to database
        return guardianRepository.save(guardianToDelete);
    }

    @Transactional
    public Guardian deleteGuardian(Integer id) {
        return deactivateGuardian(id, null);
    }

    public Guardian uploadProfilePicture(Integer id, MultipartFile file) {
        Guardian guardian = getGuardianById(id);
        guardian.setProfilePicture(profilePictureStorageService.save(file, guardian.getProfilePicture(), "guardians"));
        return guardianRepository.save(guardian);
    }

    private void validateGuardian(Guardian guardian) {
        if (guardian.getFullName() == null || guardian.getFullName().trim().split("\\s+").length < 2) {
            throw new RuntimeException("Guardian full name must contain at least two words");
        }
        if (guardian.getDob() == null || guardian.getGender() == null) {
            throw new RuntimeException("Guardian date of birth and gender are required");
        }
        if (guardian.getDob().plusYears(18).isAfter(LocalDate.now())) {
            throw new RuntimeException("Guardian must be at least 18 years old");
        }
        if (guardian.getPhonePrimary() == null || !guardian.getPhonePrimary().matches("07\\d{8}")) {
            throw new RuntimeException("Primary phone must contain 10 digits and start with 07");
        }
        if (!isBlank(guardian.getWhatsappNumber()) && !guardian.getWhatsappNumber().matches("07\\d{8}"))
            throw new IllegalArgumentException("WhatsApp number must contain 10 digits and start with 07");
        if (!isBlank(guardian.getEmail()) && !guardian.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Enter a valid guardian email address");
        if (guardian.getAddress() == null || guardian.getAddress().trim().length() < 10)
            throw new IllegalArgumentException("Guardian address must contain at least 10 characters");

        String nic = guardian.getNic() == null ? "" : guardian.getNic().trim();
        boolean oldFormat = nic.matches("\\d{9}[vVxX]");
        boolean newFormat = nic.matches("\\d{12}");
        if (!oldFormat && !newFormat) throw new RuntimeException("Invalid NIC format");
        int year = oldFormat ? 1900 + Integer.parseInt(nic.substring(0, 2)) : Integer.parseInt(nic.substring(0, 4));
        int encodedDay = oldFormat ? Integer.parseInt(nic.substring(2, 5)) : Integer.parseInt(nic.substring(4, 7));
        Gender inferredGender = encodedDay > 500 ? Gender.FEMALE : Gender.MALE;
        int dayOfYear = encodedDay > 500 ? encodedDay - 500 : encodedDay;
        try {
            LocalDate.ofYearDay(year, dayOfYear);
            if (inferredGender != guardian.getGender()) throw new RuntimeException("Guardian gender does not match the NIC");
        } catch (DateTimeException e) {
            throw new RuntimeException("Invalid NIC birth date");
        }
    }

    private void normalizeGuardian(Guardian guardian) {
        guardian.setFullName(guardian.getFullName() == null ? null : guardian.getFullName().trim().replaceAll("\\s+", " "));
        guardian.setNic(guardian.getNic() == null ? null : guardian.getNic().trim().toUpperCase());
        guardian.setPhonePrimary(normalizePhone(guardian.getPhonePrimary()));
        guardian.setWhatsappNumber(normalizePhone(guardian.getWhatsappNumber()));
        guardian.setEmail(guardian.getEmail() == null ? null : guardian.getEmail().trim().toLowerCase());
        guardian.setAddress(guardian.getAddress() == null ? null : guardian.getAddress().trim().replaceAll("\\s+", " "));
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        String digits = phone.replaceAll("\\D", "");
        return digits.startsWith("94") && digits.length() == 11 ? "0" + digits.substring(2) : digits;
    }
    private List<com.pirivena_project.pirivena.model.Student> students(Guardian guardian) { return studentRepository.findByGuardianIdOrderByFullNameAsc(guardian.getId()); }
    private static String nullSafe(String value) { return value == null ? "" : value; }
    private static boolean isBlank(String value) { return value == null || value.isBlank(); }
    private static String mask(String value) { return isBlank(value) ? null : "•".repeat(Math.max(0, value.length() - 4)) + value.substring(Math.max(0, value.length() - 4)); }
    private static String maskEmail(String value) { if (isBlank(value)) return null; int at = value.indexOf('@'); return at < 1 ? "Restricted" : "***" + value.substring(at); }


}
