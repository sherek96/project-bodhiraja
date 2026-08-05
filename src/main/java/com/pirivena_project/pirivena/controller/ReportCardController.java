package com.pirivena_project.pirivena.controller;

// Purpose: Exposes HTTP endpoints for report card operations.

import com.pirivena_project.pirivena.dto.ReportCardResponseDTO;
import com.pirivena_project.pirivena.service.ReportCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report-cards")
@RequiredArgsConstructor
public class ReportCardController {

    private final ReportCardService reportCardService;

    // Fetch a fully aggregated terminal report card DTO configuration
    // Example lookup: /api/report-cards/enrollment/1/term/1
    @GetMapping("/enrollment/{enrollmentId}/term/{termNumber}")
    @PreAuthorize("@assignmentSecurity.reportCard(#enrollmentId, authentication)")
    public ResponseEntity<ReportCardResponseDTO> getReportCard(
            @PathVariable Integer enrollmentId,
            @PathVariable Integer termNumber) {

        ReportCardResponseDTO reportCard = reportCardService.generateReportCard(enrollmentId, termNumber);
        return ResponseEntity.ok(reportCard);
    }
}
