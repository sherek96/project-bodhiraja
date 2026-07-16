package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.TransactionIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionIncomeRepository extends JpaRepository<TransactionIncome, Integer> {
    // Allows administrators to see an audit trail of inflows for a specific fund
    List<TransactionIncome> findByFundingPoolId(Integer poolId);
}