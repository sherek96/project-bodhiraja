package com.pirivena_project.pirivena.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReturnBookRequest {
    private LocalDate returnDate;
    private Integer fundingPoolId;
    private Integer incomeCategoryId;
}
