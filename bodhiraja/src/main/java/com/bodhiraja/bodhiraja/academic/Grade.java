package com.bodhiraja.bodhiraja.academic;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "grade")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String name; // e.g., "Grade 6", "Grade 7"
}