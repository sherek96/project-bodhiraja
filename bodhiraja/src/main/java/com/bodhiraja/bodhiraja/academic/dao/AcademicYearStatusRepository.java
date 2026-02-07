package com.bodhiraja.bodhiraja.academic.dao;

import com.bodhiraja.bodhiraja.academic.AcademicYearStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicYearStatusRepository extends JpaRepository<AcademicYearStatus, Integer> {
}