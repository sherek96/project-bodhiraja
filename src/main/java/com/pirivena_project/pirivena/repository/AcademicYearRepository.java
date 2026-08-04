package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.enums.AcademicYearStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Integer> {

    // Fetches the single active timeline container for the system
    Optional<AcademicYear> findByStatus(AcademicYearStatus status);

    List<AcademicYear> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);

}
