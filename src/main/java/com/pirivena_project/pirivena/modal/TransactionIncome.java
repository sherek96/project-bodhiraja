package com.pirivena_project.pirivena.modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_income")
public class TransactionIncome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "date_received", nullable = false)
    private LocalDate dateReceived;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // --- RELATIONSHIPS ---
    @ManyToOne(optional = false)
    @JoinColumn(name = "income_category_id")
    private IncomeCategory incomeCategory;

    @ManyToOne(optional = false)
    @JoinColumn(name = "funding_pool_id")
    private FundingPool fundingPool; // Links inflows directly to our asset containers

    @Column(name = "donor_name", length = 150)
    private String donorName;

    // --- AUDIT TRAIL ---
    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @Column(name = "adduser", nullable = false)
    private Integer addUser;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
    }
}
