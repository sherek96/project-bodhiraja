package com.bodhiraja.bodhiraja.guardian.dao;

import com.bodhiraja.bodhiraja.guardian.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Integer> {

    // Search for a guardian by their NIC (National ID Card)
    Optional<Guardian> findByNic(String nic);

    // Search by Phone Number
    Optional<Guardian> findByPhone(String phone);
}