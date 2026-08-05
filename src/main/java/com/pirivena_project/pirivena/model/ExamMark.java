package com.pirivena_project.pirivena.model;

// Purpose: Represents exam mark data stored in the database.

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "exam_mark")
public class ExamMark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer termNumber; // Constrained to 1, 2, or 3 via business validation layer

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal marksObtained;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment; // Replaces individual student and classroom fields

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject; // Tracks the blueprint subject being graded
}
