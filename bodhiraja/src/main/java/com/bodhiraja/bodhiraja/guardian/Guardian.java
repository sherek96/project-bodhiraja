package com.bodhiraja.bodhiraja.guardian;

import com.bodhiraja.bodhiraja.user.User; // Import your User entity
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "guardian")
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String fullname;

    @Column(nullable = false, unique = true, length = 12)
    private String nic;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(nullable = false, length = 10)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String note;

    // --- RELATIONSHIPS ---

    @ManyToOne(optional = false)
    @JoinColumn(name = "guardiantype_id")
    private GuardianType guardianType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "guardianstatus_id")
    private GuardianStatus guardianStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id") // Links to the User who added this record
    private User user;

    // --- AUDIT FIELDS ---
    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @Column(name = "updatedate")
    private LocalDateTime updateDate;

    @Column(name = "deletedate")
    private LocalDateTime deleteDate;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}