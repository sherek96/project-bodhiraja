package com.bodhiraja.bodhiraja.academic;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "academicyear")
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 4)
    private String name; // SQL CHAR(4)

    @Column(nullable = false)
    private LocalDate startdate;

    @Column(nullable = false)
    private LocalDate enddate;

    // --- RELATIONSHIP ---
    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_year_status_id")
    private AcademicYearStatus academicYearStatus;

    // --- AUDIT ---
    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @Column(name = "adduser", nullable = false)
    private Integer addUser;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
        if (addUser == null) addUser = 1;
    }
}