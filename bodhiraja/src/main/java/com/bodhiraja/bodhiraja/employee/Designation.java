package com.bodhiraja.bodhiraja.employee;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "designation")
public class Designation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Teacher", "Principal", "Clerk"
}