package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.Enrollment;
import java.util.List;

public interface EnrollmentService {
    Enrollment enrollStudent(Enrollment enrollment);
    List<Enrollment> getAllEnrollments();
    List<Enrollment> getEnrollmentsByClassroom(Integer classroomId);
    void cancelEnrollment(Integer id);
}