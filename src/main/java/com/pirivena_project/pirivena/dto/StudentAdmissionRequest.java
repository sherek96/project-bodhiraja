package com.pirivena_project.pirivena.dto;

import com.pirivena_project.pirivena.modal.Student;
import lombok.Data;

@Data
public class StudentAdmissionRequest {
    private Student student;
    private Integer classroomId;
    private Boolean createUserAccount = false;
    private String username;
    private String temporaryPassword;
}
