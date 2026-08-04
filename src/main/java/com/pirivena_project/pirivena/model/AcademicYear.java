package com.pirivena_project.pirivena.model;

// Purpose: Represents academic year data stored in the database.

import com.pirivena_project.pirivena.enums.AcademicYearStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "academic_year")
public class AcademicYear extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name; // e.g., "2026"

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // Added for chronological order confirmation checks

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate; // Added to lock down academic boundaries cleanly

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_status", nullable = false, length = 20)
    private AcademicYearStatus status = AcademicYearStatus.PLANNED;
}
