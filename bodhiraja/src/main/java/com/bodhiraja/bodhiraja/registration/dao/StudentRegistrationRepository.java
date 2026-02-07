package com.bodhiraja.bodhiraja.registration.dao;

import com.bodhiraja.bodhiraja.registration.StudentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRegistrationRepository extends JpaRepository<StudentRegistration, Integer> {

    // 1. Get the class list (Who is in Class 6-A?)
    List<StudentRegistration> findByClassDetails_Id(Integer classId);

    // 2. Get student history (What classes was Kasun in?)
    List<StudentRegistration> findByStudent_Id(Integer studentId);
}