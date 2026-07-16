package com.pirivena_project.pirivena.modal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
public class Book extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false) private String title;
    @Column(nullable = false) private String author;
    @Column(unique = true, length = 30) private String isbn;
    private String publisher;
    @Column(nullable = false) private Integer totalCopies;
    @Column(nullable = false) private Integer availableCopies;
    @ManyToOne(optional = false) @JoinColumn(name = "book_category_id")
    private BookCategory bookCategory;
}
