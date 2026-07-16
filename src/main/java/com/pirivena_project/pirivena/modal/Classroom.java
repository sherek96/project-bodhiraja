package com.pirivena_project.pirivena.modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
        name = "classroom",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_classroom_year_teacher",
                columnNames = {"academic_year_id", "class_teacher_id"}
        )
)
public class Classroom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name; // e.g., "Grade 6-A"

    @ManyToOne
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "class_teacher_id", nullable = false)
    private Employee classTeacher;
}
