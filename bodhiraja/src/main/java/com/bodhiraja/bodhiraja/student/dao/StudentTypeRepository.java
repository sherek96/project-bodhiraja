package com.bodhiraja.bodhiraja.student.dao;

import com.bodhiraja.bodhiraja.student.StudentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentTypeRepository extends JpaRepository<StudentType, Integer> {
    // This gives you finding methods like findById() for free!
    StudentType findByName(String name); // Helper to find by "Clergy" or "Lay"
}