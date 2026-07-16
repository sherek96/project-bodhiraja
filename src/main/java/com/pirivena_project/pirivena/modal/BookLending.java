package com.pirivena_project.pirivena.modal;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_lending")
public class BookLending extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(optional = false) @JoinColumn(name = "book_id") private Book book;
    @ManyToOne(optional = false) @JoinColumn(name = "library_member_id") private LibraryMember libraryMember;
    @Column(nullable = false) private LocalDate issueDate;
    @Column(nullable = false) private LocalDate dueDate;
    private LocalDate returnDate;
    @Column(nullable = false, length = 20) private String status = "ISSUED";
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal fineAmount = BigDecimal.ZERO;
    @ManyToOne @JoinColumn(name = "income_id") private TransactionIncome fineIncome;
}
