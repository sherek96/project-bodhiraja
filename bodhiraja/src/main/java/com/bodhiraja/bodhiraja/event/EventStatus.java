package com.bodhiraja.bodhiraja.event;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "eventstatus")
public class EventStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String name; // e.g., "Scheduled", "Completed"
}