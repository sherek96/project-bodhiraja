package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.dto.PromotionRequestDTO;
import com.pirivena_project.pirivena.modal.PromotionDecision;
import java.util.List;

public interface PromotionService {
    List<PromotionDecision> recordDecisions(PromotionRequestDTO promotionRequest, String processedBy);
    List<PromotionDecision> getStudentHistory(Integer studentId);
    List<PromotionDecision> getClassroomHistory(Integer classroomId);
}
