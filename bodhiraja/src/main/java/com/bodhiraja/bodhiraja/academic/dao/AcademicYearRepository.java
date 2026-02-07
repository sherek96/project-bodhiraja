package com.bodhiraja.bodhiraja.academic.dao;

import com.bodhiraja.bodhiraja.academic.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Integer> {
    // Allows us to find a year by name, e.g., "2026"
    AcademicYear findByName(String name);
}