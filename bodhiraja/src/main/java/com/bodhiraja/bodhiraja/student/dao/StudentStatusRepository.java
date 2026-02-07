package com.bodhiraja.bodhiraja.student.dao;

import com.bodhiraja.bodhiraja.student.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentStatusRepository extends JpaRepository<StudentStatus, Integer> {
    StudentStatus findByName(String name);
}