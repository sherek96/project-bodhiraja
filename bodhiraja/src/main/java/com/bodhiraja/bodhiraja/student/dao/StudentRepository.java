package com.bodhiraja.bodhiraja.student.dao;

import com.bodhiraja.bodhiraja.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    // Find by Index Number (e.g., "ST001")
    Optional<Student> findByIndexNo(String indexNo);

    // Find by NIC (if they have one)
    Optional<Student> findByNic(String nic);

    List<Student> findByStudentStatus_Name(String statusName);
}