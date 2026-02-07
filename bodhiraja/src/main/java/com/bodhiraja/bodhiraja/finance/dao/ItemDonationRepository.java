package com.bodhiraja.bodhiraja.finance.dao;

import com.bodhiraja.bodhiraja.finance.ItemDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDonationRepository extends JpaRepository<ItemDonation, Integer> {

    // See all donations made by a specific donor
    // Note: We use the underscore to hop into the 'Donor' object and get its ID
    List<ItemDonation> findByDonor_Id(Integer donorId);
}