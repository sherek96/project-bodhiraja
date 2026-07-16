package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.modal.ClassroomSubject;
import com.pirivena_project.pirivena.repository.ClassroomSubjectRepository;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.repository.AcademicYearRepository;
import com.pirivena_project.pirivena.service.ClassroomSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomSubjectServiceImpl implements ClassroomSubjectService {

    private final ClassroomSubjectRepository classroomSubjectRepository;
    private final ClassroomRepository classroomRepository;
    private final AcademicYearRepository academicYearRepository;

    @Override
    @Transactional
    public ClassroomSubject saveClassroomSubject(ClassroomSubject classroomSubject) {
        if (classroomSubject.getClassroom() == null || classroomSubject.getClassroom().getId() == null) {
            throw new IllegalArgumentException("Validation Error: A classroom must be selected.");
        }

        Integer classroomId = classroomSubject.getClassroom().getId();
        Integer subjectId = classroomSubject.getSubject().getId();

        var activeYear = academicYearRepository.findByIsCurrentTrue()
                .orElseThrow(() -> new IllegalStateException("Validation Error: No current academic year is configured."));
        var verifiedClassroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("Validation Error: The selected classroom does not exist."));
        if (!verifiedClassroom.getAcademicYear().getId().equals(activeYear.getId())) {
            throw new IllegalStateException("Validation Error: Subjects can only be assigned to current academic year classrooms.");
        }
        classroomSubject.setClassroom(verifiedClassroom);

        // Guardrail Lookup: Verify if this subject is already allocated to this classroom
        classroomSubjectRepository.findByClassroomIdAndSubjectId(classroomId, subjectId)
                .ifPresent(existingAllocation -> {
                    // Stop execution if an assignment exists, unless it's an update to the same record
                    if (!existingAllocation.getId().equals(classroomSubject.getId())) {
                        throw new RuntimeException("Validation Error: The subject '" +
                                existingAllocation.getSubject().getName() +
                                "' is already assigned to '" +
                                existingAllocation.getClassroom().getName() + "'.");
                    }
                });

        return classroomSubjectRepository.save(classroomSubject);
    }

    @Override
    public List<ClassroomSubject> getAllClassroomSubjects() {
        return classroomSubjectRepository.findAll();
    }

    @Override
    public List<ClassroomSubject> getClassroomSubjectsByClassroom(Integer classroomId) {
        return classroomSubjectRepository.findByClassroomId(classroomId);
    }

    @Override
    @Transactional
    public void removeClassroomSubject(Integer id) {
        if (!classroomSubjectRepository.existsById(id)) {
            throw new RuntimeException("Data Deletion Error: Workload allocation ID " + id + " does not exist.");
        }
        classroomSubjectRepository.deleteById(id);
    }
}
