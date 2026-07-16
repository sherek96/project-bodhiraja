package com.pirivena_project.pirivena.dto;

import lombok.Data;
import java.util.List;

@Data
public class PromotionRequestDTO {
    private Integer sourceClassroomId;
    private Integer destinationClassroomId;
    private List<Integer> studentIds; // The array of student IDs approved for advancement
}