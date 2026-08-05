package com.pirivena_project.pirivena.service;

// Purpose: Contains the business rules for book operations.

import com.pirivena_project.pirivena.model.Book;
import com.pirivena_project.pirivena.repository.BookCategoryRepository;
import com.pirivena_project.pirivena.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookCategoryRepository categoryRepository;

    public Book save(Book book) {
        validate(book);
        if (book.getAvailableCopies() == null) book.setAvailableCopies(book.getTotalCopies());
        book.setBookCategory(categoryRepository.findById(book.getBookCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Book category was not found.")));
        return bookRepository.save(book);
    }

    private void validate(Book book) {
        if (book == null || book.getTitle() == null || book.getTitle().isBlank()
                || book.getAuthor() == null || book.getAuthor().isBlank()
                || book.getBookCategory() == null || book.getBookCategory().getId() == null) {
            throw new IllegalArgumentException("Title, author, and category are required.");
        }
        if (book.getTotalCopies() == null || book.getTotalCopies() < 1) {
            throw new IllegalArgumentException("Total copies must be at least one.");
        }
        if (book.getAvailableCopies() != null
                && (book.getAvailableCopies() < 0 || book.getAvailableCopies() > book.getTotalCopies())) {
            throw new IllegalArgumentException("Available copies must be between zero and total copies.");
        }
    }

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public void delete(Integer id) {
        bookRepository.deleteById(id);
    }
}
