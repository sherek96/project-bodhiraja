package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.dto.PromotionRequestDTO;
import com.pirivena_project.pirivena.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.pirivena_project.pirivena.modal.PromotionDecision;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // Execute an automated bulk cohort promotion migration
    @PostMapping("/batch")
    public ResponseEntity<List<PromotionDecision>> executeBatchPromotion(
            @RequestBody PromotionRequestDTO promotionRequest,
            Authentication authentication) {
        String processedBy = authentication == null ? "system" : authentication.getName();
        return ResponseEntity.ok(promotionService.recordDecisions(promotionRequest, processedBy));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PromotionDecision>> getStudentHistory(@PathVariable Integer studentId) {
        return ResponseEntity.ok(promotionService.getStudentHistory(studentId));
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<PromotionDecision>> getClassroomHistory(@PathVariable Integer classroomId) {
        return ResponseEntity.ok(promotionService.getClassroomHistory(classroomId));
    }
}
