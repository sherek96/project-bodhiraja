package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.ClassroomSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClassroomSubjectRepository extends JpaRepository<ClassroomSubject, Integer> {

    // Guards against duplicating a subject within the same classroom environment
    Optional<ClassroomSubject> findByClassroomIdAndSubjectId(Integer classroomId, Integer subjectId);

    // Fetches the entire curriculum-teacher workload matrix for a specific classroom shell
    List<ClassroomSubject> findByClassroomId(Integer classroomId);
}