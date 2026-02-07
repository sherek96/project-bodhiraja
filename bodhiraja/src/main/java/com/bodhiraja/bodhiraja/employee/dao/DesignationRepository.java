package com.bodhiraja.bodhiraja.employee.dao;

import com.bodhiraja.bodhiraja.employee.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Integer> {
}