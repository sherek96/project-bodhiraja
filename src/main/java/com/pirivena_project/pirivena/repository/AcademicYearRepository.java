package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Integer> {

    // Fetches the single active timeline container for the system
    Optional<AcademicYear> findByIsCurrentTrue();

    List<AcademicYear> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);

    // Directly resets any currently active year records back to false
    @Modifying
    @Query("UPDATE AcademicYear a SET a.isCurrent = false WHERE a.isCurrent = true")
    void deactivateAllCurrentYears();
}
