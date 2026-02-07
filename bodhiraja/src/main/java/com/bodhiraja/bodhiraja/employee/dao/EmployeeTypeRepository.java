package com.bodhiraja.bodhiraja.employee.dao;

import com.bodhiraja.bodhiraja.employee.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, Integer> {
}