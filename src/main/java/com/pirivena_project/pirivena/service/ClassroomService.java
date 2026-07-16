package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.Classroom;
import java.util.List;

public interface ClassroomService {
    Classroom saveClassroom(Classroom classroom);
    List<Classroom> getAllClassrooms();
    List<Classroom> getClassroomsByAcademicYear(Integer academicYearId);
    Classroom getClassroomById(Integer id);
    void deleteClassroom(Integer id);
}