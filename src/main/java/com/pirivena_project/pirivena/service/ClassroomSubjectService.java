package com.pirivena_project.pirivena.service;

// Purpose: Contains the business rules for classroom subject operations.

import com.pirivena_project.pirivena.model.ClassroomSubject;
import com.pirivena_project.pirivena.repository.ClassroomSubjectRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.service.AcademicYearLifecycleGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomSubjectService {

    private final ClassroomSubjectRepository classroomSubjectRepository;
    private final ClassroomRepository classroomRepository;
    private final AcademicYearLifecycleGuard lifecycleGuard;

    @Transactional
    public ClassroomSubject saveClassroomSubject(ClassroomSubject classroomSubject) {
        if (classroomSubject.getId() != null) {
            ClassroomSubject existing = classroomSubjectRepository.findById(classroomSubject.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Validation Error: Subject allocation does not exist."));
            lifecycleGuard.requireStructureEditable(
                    existing.getClassroom().getAcademicYear(), "Editing a classroom subject allocation");
        }
        if (classroomSubject.getClassroom() == null || classroomSubject.getClassroom().getId() == null) {
            throw new IllegalArgumentException("Validation Error: A classroom must be selected.");
        }
        if (classroomSubject.getSubject() == null || classroomSubject.getSubject().getId() == null) {
            throw new IllegalArgumentException("Validation Error: A subject must be selected.");
        }

        Integer classroomId = classroomSubject.getClassroom().getId();
        Integer subjectId = classroomSubject.getSubject().getId();

        var verifiedClassroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("Validation Error: The selected classroom does not exist."));
        lifecycleGuard.requireStructureEditable(
                verifiedClassroom.getAcademicYear(), "Creating or editing a classroom subject allocation");
        classroomSubject.setClassroom(verifiedClassroom);

        // Guardrail Lookup: Verify if this subject is already allocated to this classroom
        classroomSubjectRepository.findByClassroomIdAndSubjectIdAndIsActiveTrue(classroomId, subjectId)
                .ifPresent(existingAllocation -> {
                    // Stop execution if an assignment exists, unless it's an update to the same record
                    if (!existingAllocation.getId().equals(classroomSubject.getId())) {
                        throw new IllegalArgumentException("Validation Error: The subject '" +
                                existingAllocation.getSubject().getName() +
                                "' is already assigned to '" +
                                existingAllocation.getClassroom().getName() + "'.");
                    }
                });

        classroomSubject.setIsActive(true);
        return classroomSubjectRepository.save(classroomSubject);
    }

    public List<ClassroomSubject> getAllClassroomSubjects() {
        return classroomSubjectRepository.findAll().stream()
                .filter(allocation -> Boolean.TRUE.equals(allocation.getIsActive()))
                .toList();
    }

    public List<ClassroomSubject> getClassroomSubjectsByClassroom(Integer classroomId) {
        return classroomSubjectRepository.findByClassroomId(classroomId).stream()
                .filter(allocation -> Boolean.TRUE.equals(allocation.getIsActive()))
                .toList();
    }

    @Transactional
    public void removeClassroomSubject(Integer id) {
        ClassroomSubject allocation = classroomSubjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Data Deletion Error: Workload allocation ID " + id + " does not exist."));
        lifecycleGuard.requireStructureEditable(
                allocation.getClassroom().getAcademicYear(), "Removing a classroom subject allocation");
        if (Boolean.FALSE.equals(allocation.getIsActive())) return;
        allocation.setIsActive(false);
        classroomSubjectRepository.save(allocation);
    }
}
