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
@Table(name = "transaction_expense")
public class TransactionExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "date_spent", nullable = false)
    private LocalDate dateSpent;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // --- RELATIONSHIPS ---
    @ManyToOne(optional = false)
    @JoinColumn(name = "expense_category_id")
    private ExpenseCategory expenseCategory;

    @ManyToOne(optional = false)
    @JoinColumn(name = "funding_pool_id")
    private FundingPool fundingPool; // Tracks which pool balance is reduced by this outflow

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventDetails event; // References sibling EventDetails entity in the same package

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