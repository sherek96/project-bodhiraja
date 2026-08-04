package com.pirivena_project.pirivena.dto;

import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.modal.Student;

public record StudentAdmissionResponse(Student student, Enrollment enrollment, String username) {
}
