package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.dto.AcademicYearRolloverResult;
import com.pirivena_project.pirivena.dto.AcademicYearSummaryDTO;
import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.modal.AcademicYearStatus;
import com.pirivena_project.pirivena.modal.Classroom;
import com.pirivena_project.pirivena.modal.ClassroomStatus;
import com.pirivena_project.pirivena.modal.ClassroomSubject;
import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.modal.EnrollmentStatus;
import com.pirivena_project.pirivena.repository.AcademicYearRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.repository.ClassroomSubjectRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.service.AcademicYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AcademicYearServiceImpl implements AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassroomSubjectRepository classroomSubjectRepository;

    @Override
    @Transactional
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
            request.setIsCurrent(false);
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

    @Override
    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }

    @Override
    public AcademicYear getActiveAcademicYear() {
        return academicYearRepository.findByIsCurrentTrue()
                .orElseThrow(() -> new IllegalStateException("No current academic year has been established."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicYearSummaryDTO> getAcademicYearSummaries() {
        return academicYearRepository.findAll().stream().map(this::toSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicYearSummaryDTO getAcademicYearSummary(Integer id) {
        return toSummary(getYear(id));
    }

    @Override
    @Transactional
    public AcademicYear activateAcademicYear(Integer id) {
        AcademicYear target = getYear(id);
        if (target.getStatus() == AcademicYearStatus.ARCHIVED) {
            throw new IllegalStateException("An archived academic year cannot be made current.");
        }
        if (target.getStatus() == AcademicYearStatus.CURRENT) return target;
        if (target.getStatus() != AcademicYearStatus.PLANNED) {
            throw new IllegalStateException("Only a planned academic year can be made current.");
        }

        academicYearRepository.findByIsCurrentTrue().ifPresent(current -> {
            if (!current.getId().equals(target.getId())) {
                long unpromoted = countUnpromoted(current);
                if (unpromoted > 0) {
                    throw new IllegalStateException("Cannot switch the current year: " + current.getName()
                            + " has " + unpromoted + " unpromoted active student(s). Close that year after reviewing the warning first.");
                }
                current.setIsCurrent(false);
                current.setStatus(AcademicYearStatus.COMPLETED);
                academicYearRepository.save(current);
                transitionClassrooms(current.getId(), ClassroomStatus.COMPLETED);
            }
        });

        target.setStatus(AcademicYearStatus.CURRENT);
        target.setIsCurrent(true);
        transitionClassrooms(target.getId(), ClassroomStatus.ACTIVE);
        return academicYearRepository.save(target);
    }

    @Override
    @Transactional
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
        year.setIsCurrent(false);
        year.setStatus(AcademicYearStatus.COMPLETED);
        completeRemainingEnrollments(year.getId());
        transitionClassrooms(year.getId(), ClassroomStatus.COMPLETED);
        return academicYearRepository.save(year);
    }

    @Override
    @Transactional
    public AcademicYear archiveAcademicYear(Integer id) {
        AcademicYear year = getYear(id);
        if (year.getStatus() != AcademicYearStatus.COMPLETED) {
            throw new IllegalStateException("Only a completed academic year can be archived.");
        }
        year.setIsCurrent(false);
        year.setStatus(AcademicYearStatus.ARCHIVED);
        transitionClassrooms(year.getId(), ClassroomStatus.ARCHIVED);
        return academicYearRepository.save(year);
    }

    @Override
    @Transactional
    public AcademicYearRolloverResult copyStructure(Integer sourceId, Integer targetId) {
        if (sourceId.equals(targetId)) {
            throw new IllegalArgumentException("Source and target academic years must be different.");
        }
        AcademicYear source = getYear(sourceId);
        AcademicYear target = getYear(targetId);
        if (target.getStatus() != AcademicYearStatus.PLANNED) {
            throw new IllegalStateException("Classrooms can only be copied into a planned academic year.");
        }
        if (!target.getStartDate().isAfter(source.getStartDate())) {
            throw new IllegalArgumentException("The target academic year must begin after the source year.");
        }
        if (classroomRepository.countByAcademicYearIdAndStatusNot(targetId, ClassroomStatus.ARCHIVED) > 0) {
            throw new IllegalStateException("The target academic year already contains classrooms. Copying was stopped to prevent duplicates.");
        }

        List<Classroom> sourceClassrooms = classroomRepository.findByAcademicYearId(sourceId).stream()
                .filter(classroom -> classroom.getStatus() != ClassroomStatus.ARCHIVED)
                .toList();
        Map<Integer, Classroom> copiedClassrooms = new HashMap<>();
        List<Classroom> newClassrooms = new ArrayList<>();
        for (Classroom original : sourceClassrooms) {
            Classroom copy = new Classroom();
            copy.setName(original.getName());
            copy.setCapacity(original.getCapacity());
            copy.setAcademicYear(target);
            copy.setClassTeacher(original.getClassTeacher());
            copy.setStatus(ClassroomStatus.PLANNED);
            newClassrooms.add(copy);
        }
        List<Classroom> savedClassrooms = classroomRepository.saveAll(newClassrooms);
        for (int index = 0; index < sourceClassrooms.size(); index++) {
            copiedClassrooms.put(sourceClassrooms.get(index).getId(), savedClassrooms.get(index));
        }

        List<ClassroomSubject> allocations =
                classroomSubjectRepository.findByClassroomAcademicYearIdAndIsActiveTrue(sourceId);
        List<ClassroomSubject> copiedAllocations = allocations.stream().map(original -> {
            ClassroomSubject copy = new ClassroomSubject();
            copy.setClassroom(copiedClassrooms.get(original.getClassroom().getId()));
            copy.setSubject(original.getSubject());
            copy.setTeacher(original.getTeacher());
            copy.setIsActive(true);
            return copy;
        }).toList();
        classroomSubjectRepository.saveAll(copiedAllocations);
        return new AcademicYearRolloverResult(savedClassrooms.size(), copiedAllocations.size());
    }

    private AcademicYearSummaryDTO toSummary(AcademicYear year) {
        return new AcademicYearSummaryDTO(
                year.getId(), year.getName(), year.getStartDate(), year.getEndDate(), normalizedStatus(year),
                year.getIsCurrent(), classroomRepository.countByAcademicYearIdAndStatusNot(
                        year.getId(), ClassroomStatus.ARCHIVED),
                enrollmentRepository.countByClassroomAcademicYearIdAndIsActiveTrue(year.getId()),
                classroomSubjectRepository.countByClassroomAcademicYearIdAndIsActiveTrue(year.getId()),
                countUnpromoted(year));
    }

    private long countUnpromoted(AcademicYear year) {
        List<Enrollment> active = enrollmentRepository.findByClassroomAcademicYearIdAndIsActiveTrue(year.getId());
        return active.stream().filter(enrollment -> !enrollmentRepository
                .existsByStudentIdAndIsActiveTrueAndClassroomAcademicYearStartDateAfter(
                        enrollment.getStudent().getId(), year.getStartDate())).count();
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
                enrollmentRepository.findByClassroomAcademicYearIdAndIsActiveTrue(academicYearId);
        active.forEach(enrollment -> {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setIsActive(false);
        });
        enrollmentRepository.saveAll(active);
    }

    private AcademicYearStatus normalizedStatus(AcademicYear year) {
        if (year.getStatus() != null) return year.getStatus();
        return Boolean.TRUE.equals(year.getIsCurrent()) ? AcademicYearStatus.CURRENT : AcademicYearStatus.COMPLETED;
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
