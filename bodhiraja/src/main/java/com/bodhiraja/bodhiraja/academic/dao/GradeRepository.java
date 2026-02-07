package com.bodhiraja.bodhiraja.academic.dao;

import com.bodhiraja.bodhiraja.academic.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
}