package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.dto.PromotionRequestDTO;

public interface PromotionService {
    void promoteStudentRoster(PromotionRequestDTO promotionRequest);
}