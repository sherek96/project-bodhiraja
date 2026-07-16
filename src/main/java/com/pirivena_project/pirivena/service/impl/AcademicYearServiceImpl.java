package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.repository.AcademicYearRepository;
import com.pirivena_project.pirivena.service.AcademicYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AcademicYearServiceImpl implements AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    @Override
    @Transactional // Guarantees the atomicity of the active year deactivation and saving steps
    public AcademicYear saveAcademicYear(AcademicYear academicYear) {
        LocalDate startDate = academicYear.getStartDate();
        LocalDate endDate = academicYear.getEndDate();
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Academic year end date must be later than its start date.");
        }

        boolean overlaps = academicYearRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate)
                .stream()
                .anyMatch(existing -> !existing.getId().equals(academicYear.getId()));
        if (overlaps) {
            throw new IllegalArgumentException("Academic year dates overlap an existing academic year.");
        }

        // If this record is being set as current, clear the database of any other active years first
        if (Boolean.TRUE.equals(academicYear.getIsCurrent())) {
            academicYearRepository.deactivateAllCurrentYears();
        }
        return academicYearRepository.save(academicYear);
    }

    @Override
    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }

    @Override
    public AcademicYear getActiveAcademicYear() {
        return academicYearRepository.findByIsCurrentTrue()
                .orElseThrow(() -> new RuntimeException("Operational Failure: No active academic year has been established."));
    }
}
