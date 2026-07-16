package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.modal.Classroom;
import com.pirivena_project.pirivena.repository.AcademicYearRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final AcademicYearRepository academicYearRepository;

    @Override
    @Transactional
    public Classroom saveClassroom(Classroom classroom) {
        // Step 1: Intelligent Autofill Logic
        // If the frontend didn't supply an explicit year, automatically attach the active academic year
        if (classroom.getAcademicYear() == null || classroom.getAcademicYear().getId() == null) {
            AcademicYear currentActiveYear = academicYearRepository.findByIsCurrentTrue()
                    .orElseThrow(() -> new RuntimeException("Validation Error: Cannot auto-assign year. No active academic year found."));
            classroom.setAcademicYear(currentActiveYear);
        } else {
            // Re-verify the incoming year ID exists in the database
            AcademicYear verifiedYear = academicYearRepository.findById(classroom.getAcademicYear().getId())
                    .orElseThrow(() -> new RuntimeException("Validation Error: Target Academic Year does not exist."));
            classroom.setAcademicYear(verifiedYear);
        }

        // Step 2: Composite Uniqueness Guardrail
        // Check if a classroom with the exact same name already exists in this specific academic cycle
        classroomRepository.findByNameAndAcademicYear(classroom.getName(), classroom.getAcademicYear())
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

        classroomRepository.findByClassTeacherIdAndAcademicYear(
                        classroom.getClassTeacher().getId(), classroom.getAcademicYear())
                .ifPresent(existingClass -> {
                    if (!existingClass.getId().equals(classroom.getId())) {
                        throw new IllegalStateException(
                                "Validation Error: This teacher is already assigned to classroom '" +
                                        existingClass.getName() + "' for the " +
                                        classroom.getAcademicYear().getName() + " academic cycle.");
                    }
                });

        return classroomRepository.save(classroom);
    }

    @Override
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    @Override
    public List<Classroom> getClassroomsByAcademicYear(Integer academicYearId) {
        return classroomRepository.findByAcademicYearId(academicYearId);
    }

    @Override
    public Classroom getClassroomById(Integer id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data Retrieval Error: Classroom record not found for ID: " + id));
    }

    @Override
    @Transactional
    public void deleteClassroom(Integer id) {
        if (!classroomRepository.existsById(id)) {
            throw new RuntimeException("Data Deletion Error: Classroom ID " + id + " does not exist.");
        }
        classroomRepository.deleteById(id);
    }
}
