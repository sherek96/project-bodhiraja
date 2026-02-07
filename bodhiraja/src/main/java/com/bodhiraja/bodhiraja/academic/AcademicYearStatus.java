package com.bodhiraja.bodhiraja.academic;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "academic_year_status")
public class AcademicYearStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Active", "Past"
}