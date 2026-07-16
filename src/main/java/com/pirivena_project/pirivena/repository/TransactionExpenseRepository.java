package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.TransactionExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionExpenseRepository extends JpaRepository<TransactionExpense, Integer> {

    // Lists all expenses under a specific fund for tracking
    List<TransactionExpense> findByFundingPoolId(Integer poolId);

    // CRITICAL GUARDRAIL QUERY: Sums up every expense logged against an event.
    // COALESCE ensures that if no expenses exist yet, it returns 0.00 instead of null.
    @Query("SELECT COALESCE(SUM(te.amount), 0) FROM TransactionExpense te WHERE te.event.id = :eventId")
    BigDecimal getTotalSpentByEvent(@Param("eventId") Integer eventId);
}