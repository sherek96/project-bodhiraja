package com.bodhiraja.bodhiraja.registration.dao;

import com.bodhiraja.bodhiraja.registration.StudentRegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRegistrationStatusRepository extends JpaRepository<StudentRegistrationStatus, Integer> {
}