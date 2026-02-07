package com.bodhiraja.bodhiraja.finance.dao;

import com.bodhiraja.bodhiraja.finance.TransactionExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionExpenseRepository extends JpaRepository<TransactionExpense, Integer> {

    // 1. Report: Get all expenses between two dates
    List<TransactionExpense> findByDateSpentBetween(LocalDate startDate, LocalDate endDate);

    // 2. Project Budget: See all expenses linked to a specific Event
    List<TransactionExpense> findByEventDetails_Id(Integer eventId);
}