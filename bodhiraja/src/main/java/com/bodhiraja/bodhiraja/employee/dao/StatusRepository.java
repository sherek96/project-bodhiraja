package com.bodhiraja.bodhiraja.employee.dao;

import com.bodhiraja.bodhiraja.employee.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
}