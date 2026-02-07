package com.bodhiraja.bodhiraja.academic;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "classdetailsstatus")
public class ClassDetailsStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Active", "Finished"
}