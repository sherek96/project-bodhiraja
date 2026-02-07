package com.bodhiraja.bodhiraja.guardian.dao;

import com.bodhiraja.bodhiraja.guardian.GuardianStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianStatusRepository extends JpaRepository<GuardianStatus, Integer> {
}