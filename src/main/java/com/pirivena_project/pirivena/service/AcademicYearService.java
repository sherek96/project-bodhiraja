package com.pirivena_project.pirivena.service;

// Purpose: Contains the business rules for academic year operations.

import com.pirivena_project.pirivena.dto.AcademicYearSummaryDTO;
import com.pirivena_project.pirivena.model.AcademicYear;
import com.pirivena_project.pirivena.enums.AcademicYearStatus;
import com.pirivena_project.pirivena.model.Classroom;
import com.pirivena_project.pirivena.enums.ClassroomStatus;
import com.pirivena_project.pirivena.model.Enrollment;
import com.pirivena_project.pirivena.enums.EnrollmentStatus;
import com.pirivena_project.pirivena.repository.AcademicYearRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.repository.ClassroomSubjectRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassroomSubjectRepository classroomSubjectRepository;

    @Transactional
    // Create a year or update its dates and name while it is still editable.
    public AcademicYear saveAcademicYear(AcademicYear request) {
        validateDates(request);
        boolean overlaps = academicYearRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(request.getEndDate(), request.getStartDate())
                .stream()
                .anyMatch(existing -> request.getId() == null || !existing.getId().equals(request.getId()));
        if (overlaps) {
            throw new IllegalArgumentException("Academic year dates overlap an existing academic year.");
        }

        if (request.getId() == null) {
            request.setStatus(AcademicYearStatus.PLANNED);
            return academicYearRepository.save(request);
        }

        AcademicYear existing = getYear(request.getId());
        if (existing.getStatus() == AcademicYearStatus.ARCHIVED) {
            throw new IllegalStateException("Archived academic years cannot be edited.");
        }
        existing.setName(request.getName().trim());
        existing.setStartDate(request.getStartDate());
        existing.setEndDate(request.getEndDate());
        return academicYearRepository.save(existing);
    }

    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }

    // Return the one year marked CURRENT. Operations use this as the working year.
    public AcademicYear getActiveAcademicYear() {
        return academicYearRepository.findByStatus(AcademicYearStatus.CURRENT)
                .orElseThrow(() -> new IllegalStateException("No current academic year has been established."));
    }

    @Transactional(readOnly = true)
    public List<AcademicYearSummaryDTO> getAcademicYearSummaries() {
        return academicYearRepository.findAll().stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public AcademicYearSummaryDTO getAcademicYearSummary(Integer id) {
        return toSummary(getYear(id));
    }

    @Transactional
    // Make a planned year current and complete the previously current year.
    public AcademicYear activateAcademicYear(Integer id) {
        AcademicYear target = getYear(id);
        if (target.getStatus() == AcademicYearStatus.ARCHIVED) {
            throw new IllegalStateException("An archived academic year cannot be made current.");
        }
        if (target.getStatus() == AcademicYearStatus.CURRENT) return target;
        if (target.getStatus() != AcademicYearStatus.PLANNED) {
            throw new IllegalStateException("Only a planned academic year can be made current.");
        }

        academicYearRepository.findByStatus(AcademicYearStatus.CURRENT).ifPresent(current -> {
            if (!current.getId().equals(target.getId())) {
                long unpromoted = countUnpromoted(current);
                if (unpromoted > 0) {
                    throw new IllegalStateException("Cannot switch the current year: " + current.getName()
                            + " has " + unpromoted + " unpromoted active student(s). Close that year after reviewing the warning first.");
                }
                current.setStatus(AcademicYearStatus.COMPLETED);
                academicYearRepository.save(current);
                transitionClassrooms(current.getId(), ClassroomStatus.COMPLETED);
            }
        });

        target.setStatus(AcademicYearStatus.CURRENT);
        transitionClassrooms(target.getId(), ClassroomStatus.ACTIVE);
        return academicYearRepository.save(target);
    }

    @Transactional
    // Complete a current year after checking for students without promotion decisions.
    public AcademicYear closeAcademicYear(Integer id, boolean force) {
        AcademicYear year = getYear(id);
        if (year.getStatus() != AcademicYearStatus.CURRENT) {
            throw new IllegalStateException("Only the current academic year can be closed.");
        }
        long unpromoted = countUnpromoted(year);
        if (unpromoted > 0 && !force) {
            throw new IllegalStateException("Academic year has " + unpromoted
                    + " unpromoted active student(s). Confirm the warning to close it anyway.");
        }
        year.setStatus(AcademicYearStatus.COMPLETED);
        completeRemainingEnrollments(year.getId());
        transitionClassrooms(year.getId(), ClassroomStatus.COMPLETED);
        return academicYearRepository.save(year);
    }

    @Transactional
    // Archive a completed year so no further changes are possible.
    public AcademicYear archiveAcademicYear(Integer id) {
        AcademicYear year = getYear(id);
        if (year.getStatus() != AcademicYearStatus.COMPLETED) {
            throw new IllegalStateException("Only a completed academic year can be archived.");
        }
        year.setStatus(AcademicYearStatus.ARCHIVED);
        transitionClassrooms(year.getId(), ClassroomStatus.ARCHIVED);
        return academicYearRepository.save(year);
    }

    // Combine year information with its classroom, student and subject totals.
    private AcademicYearSummaryDTO toSummary(AcademicYear year) {
        return new AcademicYearSummaryDTO(
                year.getId(), year.getName(), year.getStartDate(), year.getEndDate(), year.getStatus(),
                classroomRepository.countByAcademicYearIdAndStatusNot(
                        year.getId(), ClassroomStatus.ARCHIVED),
                enrollmentRepository.countByClassroomAcademicYearIdAndStatus(year.getId(), EnrollmentStatus.ACTIVE),
                classroomSubjectRepository.countByClassroomAcademicYearIdAndIsActiveTrue(year.getId()),
                countUnpromoted(year));
    }

    private long countUnpromoted(AcademicYear year) {
        List<Enrollment> active = enrollmentRepository.findByClassroomAcademicYearIdAndStatus(year.getId(), EnrollmentStatus.ACTIVE);
        return active.stream().filter(enrollment -> !enrollmentRepository
                .existsByStudentIdAndStatusAndClassroomAcademicYearStartDateAfter(
                        enrollment.getStudent().getId(), EnrollmentStatus.ACTIVE, year.getStartDate())).count();
    }

    private void transitionClassrooms(Integer academicYearId, ClassroomStatus status) {
        List<Classroom> classrooms = classroomRepository.findByAcademicYearId(academicYearId);
        classrooms.stream()
                .filter(classroom -> classroom.getStatus() != ClassroomStatus.ARCHIVED)
                .forEach(classroom -> classroom.setStatus(status));
        classroomRepository.saveAll(classrooms);
    }

    private void completeRemainingEnrollments(Integer academicYearId) {
        List<Enrollment> active =
                enrollmentRepository.findByClassroomAcademicYearIdAndStatus(academicYearId, EnrollmentStatus.ACTIVE);
        active.forEach(enrollment -> {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        });
        enrollmentRepository.saveAll(active);
    }

    private AcademicYear getYear(Integer id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Academic year not found: " + id));
    }

    private void validateDates(AcademicYear year) {
        if (year.getName() == null || year.getName().isBlank()) {
            throw new IllegalArgumentException("Academic year name is required.");
        }
        LocalDate start = year.getStartDate();
        LocalDate end = year.getEndDate();
        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("Academic year end date must be later than its start date.");
        }
    }
}
