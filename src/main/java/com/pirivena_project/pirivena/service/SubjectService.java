package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.Subject;
import java.util.List;

public interface SubjectService {
    Subject saveSubject(Subject subject);
    List<Subject> getAllSubjects();
    Subject getSubjectById(Integer id);
    void deleteSubject(Integer id);
}