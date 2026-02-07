package com.bodhiraja.bodhiraja.registration;

import com.bodhiraja.bodhiraja.academic.ClassDetails; // The Class
import com.bodhiraja.bodhiraja.student.Student;       // The Student
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "studentregistration")
public class StudentRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer regno; // Admission/Register Number

    @Column(nullable = false)
    private LocalDate registerdate;

    @Column(columnDefinition = "TEXT")
    private String note;

    // --- RELATIONSHIPS ---

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classdetails_id")
    private ClassDetails classDetails;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_registration_status_id")
    private StudentRegistrationStatus studentRegistrationStatus;

    // --- AUDIT ---

    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @Column(name = "adduser", nullable = false)
    private Integer addUser;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
        // I've left 'addUser' logic out as you requested.
        // Remember to set it manually in your Controller!
    }
}