package com.bodhiraja.bodhiraja.student;

import com.bodhiraja.bodhiraja.academic.Grade;
import com.bodhiraja.bodhiraja.guardian.Guardian; // Import Guardian
import com.bodhiraja.bodhiraja.user.User;         // Import User
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "indexno", nullable = false, unique = true, length = 8)
    private String indexNo;

    @Column(nullable = false, length = 255)
    private String fullname;

    @Column(name = "namewithinitial", nullable = false, length = 150)
    private String nameWithInitial;

    @Column(length = 12, unique = true) // Nullable because young students might not have NIC yet
    private String nic;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(columnDefinition = "TEXT")
    private String note;

    // --- RELATIONSHIPS ---

    @ManyToOne(optional = false)
    @JoinColumn(name = "grade_id") // What grade are they currently in?
    private Grade grade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "studentstatus_id") // The link we fixed earlier!
    private StudentStatus studentStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_type_id")
    private StudentType studentType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "guardian_id") // Links to the Guardian module
    private Guardian guardian;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id") // Who added this student?
    private User user;


    // --- AUDIT FIELDS ---
    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @Column(name = "updatedate")
    private LocalDateTime updateDate;

    @Column(name = "deletedate")
    private LocalDateTime deleteDate;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}