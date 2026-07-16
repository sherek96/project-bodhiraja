package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.Employee;
import com.pirivena_project.pirivena.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsByPhonePrimary(String phone);
    boolean existsByEmail(String email);
    boolean existsByNic(String nic);


    @Query("select max(e.empNo) from Employee e")
    String findMaxEmpNo();

    boolean existsByDesignation_CodeAndStatus(String code, EmployeeStatus status);
    boolean existsByNicAndIdNot(String nic, Integer id);
    boolean existsByPhonePrimaryAndIdNot(String phone, Integer id);
    boolean existsByEmailAndIdNot(String email, Integer id);
    boolean existsByDesignation_CodeAndStatusAndIdNot(String code, EmployeeStatus status, Integer id);
}
