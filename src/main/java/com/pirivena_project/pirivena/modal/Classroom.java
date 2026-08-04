package com.pirivena_project.pirivena.modal;

import com.pirivena_project.pirivena.enums.ClassroomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "classroom")
public class Classroom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name; // e.g., "Grade 6-A"

    @Column(nullable = false)
    private Integer capacity = 40;

    @ManyToOne
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "class_teacher_id", nullable = false)
    private Employee classTeacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClassroomStatus status = ClassroomStatus.PLANNED;
}
