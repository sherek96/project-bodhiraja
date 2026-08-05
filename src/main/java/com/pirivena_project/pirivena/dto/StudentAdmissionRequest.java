package com.pirivena_project.pirivena.dto;

// Purpose: Carries student admission input from the frontend to the backend.

import com.pirivena_project.pirivena.model.Student;
import lombok.Data;

@Data
public class StudentAdmissionRequest {
    private Student student;
    private Integer classroomId;
    private Boolean createUserAccount = false;
    private String username;
    private String temporaryPassword;
}
