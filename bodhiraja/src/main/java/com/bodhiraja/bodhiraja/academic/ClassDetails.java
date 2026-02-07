package com.bodhiraja.bodhiraja.academic;

import com.bodhiraja.bodhiraja.employee.Employee;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "classdetails")
public class ClassDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name; // e.g., "6-A"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String location;

    @Column(name = "maxstudentcount", nullable = false)
    private Integer maxStudentCount;

    @Column(columnDefinition = "TEXT")
    private String note;

    // --- RELATIONSHIPS ---

    @ManyToOne(optional = false)
    @JoinColumn(name = "academicyear_id")
    private AcademicYear academicYear;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grade_id")
    private Grade grade; // Uses the Grade file we just created above

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private Employee teacher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classdetailsstatus_id")
    private ClassDetailsStatus classDetailsStatus;

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