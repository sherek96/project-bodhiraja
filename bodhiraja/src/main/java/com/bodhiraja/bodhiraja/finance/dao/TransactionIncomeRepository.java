package com.bodhiraja.bodhiraja.finance.dao;

import com.bodhiraja.bodhiraja.finance.TransactionIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionIncomeRepository extends JpaRepository<TransactionIncome, Integer> {

    // 1. Report: Get all income between two dates (e.g., Jan 1 to Jan 31)
    List<TransactionIncome> findByDateReceivedBetween(LocalDate startDate, LocalDate endDate);

    // 2. Filter: Get income by type (e.g., show me only "GOVT_GRANT")
    List<TransactionIncome> findBySourceType(String sourceType);
}