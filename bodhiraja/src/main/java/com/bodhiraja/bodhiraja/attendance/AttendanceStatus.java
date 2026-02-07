package com.bodhiraja.bodhiraja.attendance;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "attendancestatus") // Fixed: No underscore!
public class AttendanceStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String name; // e.g., "Conducted", "Cancelled"
}