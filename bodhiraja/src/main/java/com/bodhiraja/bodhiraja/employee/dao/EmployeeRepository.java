package com.bodhiraja.bodhiraja.employee.dao;

import com.bodhiraja.bodhiraja.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // Find by Employee Number (e.g., "EMP001"). Optional for not giving null point exception when wrong id present
    Optional<Employee> findByEmployeeNo(String employeeno);

    // Find by NIC (National ID)
    Optional<Employee> findByNic(String nic);

    // Find by Email (Useful if they use email to recover passwords)
    Optional<Employee> findByEmail(String email);
}