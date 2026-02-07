package com.bodhiraja.bodhiraja.academic.dao;

import com.bodhiraja.bodhiraja.academic.ClassDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassDetailsRepository extends JpaRepository<ClassDetails, Integer> {
    // Useful query: Find a class by its exact name
    ClassDetails findByName(String name);
}