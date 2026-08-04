package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes book category records in the database.
import com.pirivena_project.pirivena.model.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Integer> {
    boolean existsByNameIgnoreCase(String name);
    Optional<BookCategory> findByNameIgnoreCase(String name);
}
