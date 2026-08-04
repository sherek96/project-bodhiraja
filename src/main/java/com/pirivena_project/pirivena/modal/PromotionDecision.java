package com.pirivena_project.pirivena.modal;

import com.pirivena_project.pirivena.enums.PromotionOutcome;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "promotion_decision")
public class PromotionDecision extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_enrollment_id", nullable = false)
    private Enrollment sourceEnrollment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_classroom_id", nullable = false)
    private Classroom sourceClassroom;

    @ManyToOne
    @JoinColumn(name = "destination_classroom_id")
    private Classroom destinationClassroom;

    @ManyToOne(optional = false)
    @JoinColumn(name = "previous_academic_year_id", nullable = false)
    private AcademicYear previousAcademicYear;

    @ManyToOne
    @JoinColumn(name = "next_academic_year_id")
    private AcademicYear nextAcademicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PromotionOutcome outcome;

    @Column(name = "decision_date", nullable = false)
    private LocalDate decisionDate;

    @Column(name = "decision_reason", nullable = false, length = 500)
    private String decisionReason;

    @Column(name = "processed_by", nullable = false, length = 100)
    private String processedBy;

    @Column(name = "attendance_percentage", precision = 5, scale = 2)
    private BigDecimal attendancePercentage;

    @Column(name = "attendance_recorded_days", nullable = false)
    private Integer attendanceRecordedDays = 0;

    @Column(name = "examination_average", precision = 5, scale = 2)
    private BigDecimal examinationAverage;

    @Column(name = "examination_subject_count", nullable = false)
    private Integer examinationSubjectCount = 0;

    @Column(length = 1000)
    private String remarks;
}
