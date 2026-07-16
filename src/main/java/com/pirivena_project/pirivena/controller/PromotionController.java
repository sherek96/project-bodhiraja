package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.dto.PromotionRequestDTO;
import com.pirivena_project.pirivena.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // Execute an automated bulk cohort promotion migration
    @PostMapping("/batch")
    public ResponseEntity<String> executeBatchPromotion(@RequestBody PromotionRequestDTO promotionRequest) {
        promotionService.promoteStudentRoster(promotionRequest);
        return ResponseEntity.ok("Success: Roster cohort batch promotion completed successfully.");
    }
}
