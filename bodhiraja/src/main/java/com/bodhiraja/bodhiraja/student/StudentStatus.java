package com.bodhiraja.bodhiraja.student;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "studentstatus")
public class StudentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Active", "Suspended", "Left"
}