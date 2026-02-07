package com.bodhiraja.bodhiraja.academic.dao;

import com.bodhiraja.bodhiraja.academic.ClassDetailsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassDetailsStatusRepository extends JpaRepository<ClassDetailsStatus, Integer> {
}