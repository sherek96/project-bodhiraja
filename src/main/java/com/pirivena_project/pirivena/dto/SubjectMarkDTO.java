package com.pirivena_project.pirivena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectMarkDTO {
    private String subjectName;
    private BigDecimal marksObtained;
    private String status; // "Pass" or "Fail"
}