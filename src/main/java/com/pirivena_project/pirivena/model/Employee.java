package com.pirivena_project.pirivena.model;

// Purpose: Represents employee data stored in the database.

import com.pirivena_project.pirivena.enums.EmployeeStatus;
import com.pirivena_project.pirivena.enums.TeacherGrade;
import com.pirivena_project.pirivena.enums.Title;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "employee")
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "emp_no", unique = true, nullable = false)
    private String empNo;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "nic", unique = true , nullable = false)
    private String nic;

    @Enumerated(EnumType.STRING)
    @Column(name = "title") // e.g., "Ven.", "Mr.", "Ms."
    private Title title;

    @Column(name = "phone_primary", nullable = false)
    private String phonePrimary;

    @Column(name = "phone_secondary")
    private String phoneSecondary;

    @Column(name = "email_personal", nullable = false)
    private String email;

    @Column(name= "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "teacher_grade", nullable = false)
    private TeacherGrade teacherGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmployeeStatus status;
}
