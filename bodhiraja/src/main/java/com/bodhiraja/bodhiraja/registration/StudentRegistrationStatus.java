package com.bodhiraja.bodhiraja.registration;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "student_registration_status")
public class StudentRegistrationStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String name; // e.g., "Active", "Left School", "Transferred"
}