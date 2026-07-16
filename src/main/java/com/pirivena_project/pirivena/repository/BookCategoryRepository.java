package com.pirivena_project.pirivena.repository;
import com.pirivena_project.pirivena.modal.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Integer> {
    boolean existsByNameIgnoreCase(String name);
    Optional<BookCategory> findByNameIgnoreCase(String name);
}
