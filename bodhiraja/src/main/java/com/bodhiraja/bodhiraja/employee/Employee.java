package com.bodhiraja.bodhiraja.employee;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "employeeno", nullable = false, unique = true, length = 8)
    private String employeeNo; // e.g., "EMP0001"

    @Column(name = "fullname", nullable = false, length = 150)
    private String fullName;

    @Column(name = "namewithinitial", nullable = false, length = 150)
    private String nameWithInitial;

    @Column(nullable = false, unique = true, length = 12)
    private String nic;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false, length = 10)
    private String mobile;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String note;

    // --- RELATIONSHIPS (These connect to your other tables) ---

    @ManyToOne(optional = false)
    @JoinColumn(name = "designation_id") // This matches your SQL column name exactly
    private Designation designation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employeetype_id")
    private EmployeeType employeeType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id")
    private Status status;

    // --- AUDIT FIELDS ---

    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;



    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
    }
}