package com.pirivena_project.pirivena.dto;

// Purpose: Carries student admission results from the backend to the frontend.

import com.pirivena_project.pirivena.model.Enrollment;
import com.pirivena_project.pirivena.model.Student;

public record StudentAdmissionResponse(Student student, Enrollment enrollment, String username) {
}
