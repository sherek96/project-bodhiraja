package com.bodhiraja.bodhiraja.finance.dao;

import com.bodhiraja.bodhiraja.finance.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Integer> {

    // Find a donor by their contact number (Useful for "Does this donor exist?" checks)
    Optional<Donor> findByContactNo(String contactNo);
}