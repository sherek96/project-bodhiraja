package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.dto.ReportCardResponseDTO;

public interface ReportCardService {
    ReportCardResponseDTO generateReportCard(Integer enrollmentId, Integer termNumber);
}