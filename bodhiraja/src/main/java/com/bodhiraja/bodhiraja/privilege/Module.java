package com.bodhiraja.bodhiraja.privilege;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "module")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String name;
}