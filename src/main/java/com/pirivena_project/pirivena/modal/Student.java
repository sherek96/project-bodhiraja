package com.pirivena_project.pirivena.modal;

import com.pirivena_project.pirivena.enums.StudentStatus;
import com.pirivena_project.pirivena.enums.StudentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "student")
public class Student extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "admission_no", unique = true, nullable = false)
    private String admissionNo;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_type", nullable = false)
    private StudentType studentType; // MONK or LAY

    @Column(name = "date_of_ordination")
    private LocalDate dateOfOrdination;

    @Column(name = "ordination_name")
    private String ordinationName;

    @Column(name = "previous_school")
    private String previousSchool;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StudentStatus status;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id")
    private Guardian guardian;
}
