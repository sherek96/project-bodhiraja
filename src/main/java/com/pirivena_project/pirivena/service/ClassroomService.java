package com.pirivena_project.pirivena.service;

// Purpose: Contains the business rules for classroom operations.

import com.pirivena_project.pirivena.model.AcademicYear;
import com.pirivena_project.pirivena.model.Classroom;
import com.pirivena_project.pirivena.enums.ClassroomStatus;
import com.pirivena_project.pirivena.repository.AcademicYearRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.service.AcademicYearLifecycleGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearLifecycleGuard lifecycleGuard;

    @Transactional
    public Classroom saveClassroom(Classroom classroom) {
        if (classroom.getId() != null) {
            Classroom existing = classroomRepository.findById(classroom.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Validation Error: Classroom does not exist."));
            lifecycleGuard.requireStructureEditable(existing.getAcademicYear(), "Editing a classroom");
            if (existing.getStatus() == ClassroomStatus.ARCHIVED) {
                throw new IllegalStateException("Archived classrooms are read-only.");
            }
        }
        if (classroom.getCapacity() == null) classroom.setCapacity(40);
        if (classroom.getCapacity() < 1 || classroom.getCapacity() > 500) {
            throw new IllegalArgumentException("Validation Error: Classroom capacity must be between 1 and 500.");
        }
        // Step 1: Intelligent Autofill Logic
        // If the frontend didn't supply an explicit year, automatically attach the active academic year
        if (classroom.getAcademicYear() == null || classroom.getAcademicYear().getId() == null) {
            AcademicYear currentActiveYear = academicYearRepository.findByStatus(com.pirivena_project.pirivena.enums.AcademicYearStatus.CURRENT)
                    .orElseThrow(() -> new RuntimeException("Validation Error: Cannot auto-assign year. No active academic year found."));
            classroom.setAcademicYear(currentActiveYear);
        } else {
            // Re-verify the incoming year ID exists in the database
            AcademicYear verifiedYear = academicYearRepository.findById(classroom.getAcademicYear().getId())
                    .orElseThrow(() -> new RuntimeException("Validation Error: Target Academic Year does not exist."));
            classroom.setAcademicYear(verifiedYear);
        }
        lifecycleGuard.requireStructureEditable(classroom.getAcademicYear(), "Creating or editing a classroom");

        // Step 2: Composite Uniqueness Guardrail
        // Check if a classroom with the exact same name already exists in this specific academic cycle
        classroomRepository.findFirstByNameAndAcademicYearAndStatusNot(
                        classroom.getName(), classroom.getAcademicYear(), ClassroomStatus.ARCHIVED)
                .ifPresent(existingClass -> {
                    // If it's a new record or a different record trying to claim the same name combo
                    if (!existingClass.getId().equals(classroom.getId())) {
                        throw new RuntimeException("Validation Error: A classroom named '" + classroom.getName() +
                                "' already exists for the " + classroom.getAcademicYear().getName() + " academic cycle.");
                    }
                });

        if (classroom.getClassTeacher() == null || classroom.getClassTeacher().getId() == null) {
            throw new IllegalArgumentException("Validation Error: A class teacher must be assigned.");
        }

        classroomRepository.findFirstByClassTeacherIdAndAcademicYearAndStatusNot(
                        classroom.getClassTeacher().getId(), classroom.getAcademicYear(), ClassroomStatus.ARCHIVED)
                .ifPresent(existingClass -> {
                    if (!existingClass.getId().equals(classroom.getId())) {
                        throw new IllegalStateException(
                                "Validation Error: This teacher is already assigned to classroom '" +
                                        existingClass.getName() + "' for the " +
                                        classroom.getAcademicYear().getName() + " academic cycle.");
                    }
                });

        if (classroom.getId() == null) {
            classroom.setStatus(ClassroomStatus.PLANNED);
        } else {
            ClassroomStatus persistedStatus = classroomRepository.findById(classroom.getId())
                    .map(Classroom::getStatus).orElse(ClassroomStatus.PLANNED);
            classroom.setStatus(persistedStatus);
        }
        return classroomRepository.save(classroom);
    }

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public List<Classroom> getClassroomsByAcademicYear(Integer academicYearId) {
        return classroomRepository.findByAcademicYearId(academicYearId);
    }

    public Classroom getClassroomById(Integer id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data Retrieval Error: Classroom record not found for ID: " + id));
    }

    @Transactional
    public void deleteClassroom(Integer id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Data Deletion Error: Classroom ID " + id + " does not exist."));
        lifecycleGuard.requireStructureEditable(classroom.getAcademicYear(), "Deleting a classroom");
        if (classroom.getStatus() == ClassroomStatus.ARCHIVED) return;
        classroom.setStatus(ClassroomStatus.ARCHIVED);
        classroomRepository.save(classroom);
    }
}
