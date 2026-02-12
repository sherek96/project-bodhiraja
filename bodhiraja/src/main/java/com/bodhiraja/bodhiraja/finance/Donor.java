package com.bodhiraja.bodhiraja.finance;

import com.bodhiraja.bodhiraja.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "donor")
public class Donor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "contact_no", nullable = false, length = 10)
    private String contactNo;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous; // Matches TINYINT in SQL

    // --- AUDIT ---
    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "adduser")
    private User addUser;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
    }
}