package com.bodhiraja.bodhiraja.user;

import com.bodhiraja.bodhiraja.employee.Employee; // Import the Employee we just made!
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.bodhiraja.bodhiraja.privilege.Role;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String username;

    @Column(nullable = false, length = 150)
    private String password; // In a real app, this will be encrypted

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "photopath", length = 150)
    private String photoPath;

    @Column(nullable = false)
    private Boolean status; // TINYINT(1) in MySQL maps to Boolean in Java (Active/Inactive)

    @Column(columnDefinition = "TEXT")
    private String note;

    // --- RELATIONSHIP: Link to Employee ---
    // One User Account belongs to One Employee
    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // --- AUDIT FIELDS ---
    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
        if (status == null) status = true; // Default to Active
    }
    // --- SECURITY ROLES ---

    @ManyToMany
    @JoinTable(
            name = "user_has_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private java.util.Set<Role> roles;
}