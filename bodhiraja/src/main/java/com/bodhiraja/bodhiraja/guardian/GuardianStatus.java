package com.bodhiraja.bodhiraja.guardian;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "guardianstatus")
public class GuardianStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Active", "Deceased"
}