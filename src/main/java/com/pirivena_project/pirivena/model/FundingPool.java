package com.pirivena_project.pirivena.model;

// Purpose: Represents funding pool data stored in the database.

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "funding_pool")
public class FundingPool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 150)
    private String name; // e.g., "Government Grant Fund", "Public Infrastructure Fund"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance = BigDecimal.ZERO;
}
