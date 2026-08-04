package com.pirivena_project.pirivena.dto;

// Purpose: Carries return book input from the frontend to the backend.

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReturnBookRequest {
    private LocalDate returnDate;
}
