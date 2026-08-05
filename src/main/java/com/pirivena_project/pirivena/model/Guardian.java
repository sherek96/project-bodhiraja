package com.pirivena_project.pirivena.model;

// Purpose: Represents guardian data stored in the database.

import com.pirivena_project.pirivena.enums.GuardianStatus;
import com.pirivena_project.pirivena.enums.Title;
import com.pirivena_project.pirivena.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "guardian")
public class Guardian extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "title") // e.g., "Ven.", "Mr.", "Ms."
    private Title title;

    @Column(name = "nic", unique = true, nullable = false)
    private String nic;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "phone_primary", nullable = false)
    private String phonePrimary;

    @Column(name = "whatsapp_number", length = 10)
    private String whatsappNumber;

    @Column(name = "email", length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private GuardianStatus status;

    @Column(name = "address", nullable = false) // Needed for official correspondence
    private String address;

    @Column(name = "profile_picture")
    private String profilePicture;

   // @JsonIgnore // This stops the loop!
   // @OneToMany(mappedBy = "guardian")
   // private List<Student> students;



}
