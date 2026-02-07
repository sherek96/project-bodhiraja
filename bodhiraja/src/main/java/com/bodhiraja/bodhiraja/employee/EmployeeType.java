package com.bodhiraja.bodhiraja.employee;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "employeetype")
public class EmployeeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

}
