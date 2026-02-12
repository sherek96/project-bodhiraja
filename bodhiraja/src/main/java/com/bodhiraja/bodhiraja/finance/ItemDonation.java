package com.bodhiraja.bodhiraja.finance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "item_donation")
public class ItemDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 20)
    private String unit; // e.g., "kg", "packets"

    @Column(name = "date_received", nullable = false)
    private LocalDate dateReceived;

    // --- RELATIONSHIPS ---
    @ManyToOne(optional = false)
    @JoinColumn(name = "donor_id")
    private Donor donor;

    @Column(name = "adddate", nullable = false, updatable = false)
    private LocalDateTime addDate;

    @PrePersist
    protected void onCreate() {
        addDate = LocalDateTime.now();
    }
}