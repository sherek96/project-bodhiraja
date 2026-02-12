package com.bodhiraja.bodhiraja.finance;

import com.bodhiraja.bodhiraja.event.EventDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "transaction_expense")
public class TransactionExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "date_spent", nullable = false)
    private LocalDate dateSpent;

    @Column(nullable = false, length = 100)
    private String category; // e.g., "Utilities", "Food"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // --- RELATIONSHIPS ---
    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventDetails eventDetails; // Linked event (can be null)

    // --- AUDIT ---
    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @Column(name = "adduser", nullable = false)
    private Integer addUser;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
    }
}