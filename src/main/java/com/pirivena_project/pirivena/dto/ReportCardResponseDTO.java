package com.pirivena_project.pirivena.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReportCardResponseDTO {
    private String studentName;
    private String admissionNumber;
    private String classroomName;
    private Integer termNumber;

    private List<SubjectMarkDTO> subjectDetails;

    private BigDecimal totalMarks;
    private BigDecimal averagePercentage;
    private Integer classRank;
    private Integer totalStudentsInClass;
}