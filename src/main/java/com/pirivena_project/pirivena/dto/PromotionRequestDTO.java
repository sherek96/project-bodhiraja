package com.pirivena_project.pirivena.dto;

// Purpose: Carries the promotion request data needed by one API workflow.

import lombok.Data;
import java.util.List;
import java.time.LocalDate;
import com.pirivena_project.pirivena.enums.PromotionOutcome;

@Data
public class PromotionRequestDTO {
    private Integer sourceClassroomId;
    private Integer destinationClassroomId;
    private List<Integer> studentIds; // The array of student IDs approved for advancement
    private PromotionOutcome outcome;
    private LocalDate decisionDate;
    private String decisionReason;
    private String remarks;
}
