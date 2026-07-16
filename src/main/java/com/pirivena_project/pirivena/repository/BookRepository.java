package com.pirivena_project.pirivena.repository;
import com.pirivena_project.pirivena.modal.Book;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface BookRepository extends JpaRepository<Book, Integer> {
    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") Integer id);
}
