package com.bodhiraja.bodhiraja.guardian.dao;

import com.bodhiraja.bodhiraja.guardian.GuardianType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianTypeRepository extends JpaRepository<GuardianType, Integer> {
}