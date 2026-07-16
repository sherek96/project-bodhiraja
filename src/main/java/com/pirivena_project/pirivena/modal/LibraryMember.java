package com.pirivena_project.pirivena.modal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "library_member")
public class LibraryMember extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true, length = 40) private String membershipNo;
    @Column(nullable = false, length = 20) private String status = "ACTIVE";
    @ManyToOne @JoinColumn(name = "student_id") private Student student;
    @ManyToOne @JoinColumn(name = "employee_id") private Employee employee;
}
