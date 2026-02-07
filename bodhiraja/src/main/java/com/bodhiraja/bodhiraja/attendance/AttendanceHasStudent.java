package com.bodhiraja.bodhiraja.attendance;

import com.bodhiraja.bodhiraja.student.Student;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "attendance_has_student")
public class AttendanceHasStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // In your SQL this is TINYINT (0 or 1 usually)
    @Column(name = "precent_or_absence", nullable = false)
    private Boolean presentOrAbsence; // True = Present, False = Absent

    // --- RELATIONSHIPS ---

    @ManyToOne(optional = false)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance; // Link to the header above

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student; // Link to the specific student
}