package com.pirivena_project.pirivena.modal;

import com.pirivena_project.pirivena.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "attendance")
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate; // Renamed to align perfectly with the repository layers

    @Column(nullable = false)
    private Boolean isPresent; // true = Present, false = Absent

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    @Column(length = 500)
    private String note;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment; // The unified bridge to both student and classroom context
}
