package com.bodhiraja.bodhiraja.attendance;

import com.bodhiraja.bodhiraja.academic.ClassDetails;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "attendancedate", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "totalcount", nullable = false)
    private Integer totalCount;

    @Column(name = "precentcount", nullable = false)
    private Integer presentCount;

    @Column(name = "absencecount", nullable = false)
    private Integer absenceCount;

    @Column(columnDefinition = "TEXT")
    private String note;

    // --- RELATIONSHIPS ---

    @ManyToOne(optional = false)
    @JoinColumn(name = "classdetails_id")
    private ClassDetails classDetails;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attendancestatus_id") // Fixed: matches SQL column name
    private AttendanceStatus attendanceStatus;

    // --- AUDIT ---

    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @Column(name = "adduser", nullable = false)
    private Integer addUser;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
    }
}