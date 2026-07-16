package com.pirivena_project.pirivena.repository;
import com.pirivena_project.pirivena.modal.BookLending;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface BookLendingRepository extends JpaRepository<BookLending, Integer> {
    long countByLibraryMemberIdAndStatusIn(Integer memberId, List<String> statuses);
    boolean existsByLibraryMemberIdAndStatus(Integer memberId, String status);
    List<BookLending> findByStatusAndDueDateBefore(String status, LocalDate date);
}
