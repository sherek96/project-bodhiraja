package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes transaction income records in the database.

import com.pirivena_project.pirivena.model.TransactionIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionIncomeRepository extends JpaRepository<TransactionIncome, Integer> {
    // Allows administrators to see an audit trail of inflows for a specific fund
    List<TransactionIncome> findByFundingPoolId(Integer poolId);
}
