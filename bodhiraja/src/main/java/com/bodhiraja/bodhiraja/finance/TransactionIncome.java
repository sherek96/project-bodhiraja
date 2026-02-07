package com.bodhiraja.bodhiraja.finance;

import com.bodhiraja.bodhiraja.student.Student;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transaction_income")
public class TransactionIncome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "date_received", nullable = false)
    private LocalDate dateReceived;

    // Matches SQL ENUM('GOVT_GRANT', 'DONATION', 'OTHER')
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // --- RELATIONSHIPS (Optional: Donor might be null for Govt Grant) ---
    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // --- AUDIT ---
    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @Column(name = "adduser", nullable = false)
    private Integer addUser;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
    }

    public enum SourceType {
        GOVT_GRANT, DONATION, OTHER
    }
}