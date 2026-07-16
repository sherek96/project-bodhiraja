package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.ClassroomSubject;
import java.util.List;

public interface ClassroomSubjectService {
    ClassroomSubject saveClassroomSubject(ClassroomSubject classroomSubject);
    List<ClassroomSubject> getAllClassroomSubjects();
    List<ClassroomSubject> getClassroomSubjectsByClassroom(Integer classroomId);
    void removeClassroomSubject(Integer id);
}