package com.pirivena_project.pirivena.service;

// Purpose: Contains the business rules for transaction income operations.

import com.pirivena_project.pirivena.model.FundingPool;
import com.pirivena_project.pirivena.model.TransactionIncome;
import com.pirivena_project.pirivena.repository.FundingPoolRepository;
import com.pirivena_project.pirivena.repository.TransactionIncomeRepository;
import com.pirivena_project.pirivena.repository.IncomeCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

@Service
public class TransactionIncomeService {

    @Autowired
    private TransactionIncomeRepository incomeRepository;

    @Autowired
    private FundingPoolRepository poolRepository;

    @Autowired
    private IncomeCategoryRepository incomeCategoryRepository;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Transactional
    public TransactionIncome saveIncome(TransactionIncome income) {
        if (income.getAmount() == null || income.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income amount must be greater than zero.");
        }
        if (income.getDateReceived() == null || income.getDescription() == null || income.getDescription().isBlank() || income.getIncomeCategory() == null || income.getIncomeCategory().getId() == null) {
            throw new IllegalArgumentException("Income date, description, and category are required.");
        }
        // 1. Fetch and validate the associated Funding Pool
        if (income.getFundingPool() == null || income.getFundingPool().getId() == null) {
            throw new IllegalArgumentException("Income must be allocated to a valid Funding Pool.");
        }

        FundingPool pool = poolRepository.findByIdForUpdate(income.getFundingPool().getId())
                .orElseThrow(() -> new IllegalArgumentException("Target Funding Pool not found."));

        // 2. Adjust the pool's live treasury balance upward
        BigDecimal updatedBalance = pool.getCurrentBalance().add(income.getAmount());
        pool.setCurrentBalance(updatedBalance);
        poolRepository.save(pool);

        // 3. Link the managed pool entity back to the transaction and persist
        income.setFundingPool(pool);
        income.setIncomeCategory(incomeCategoryRepository.findById(income.getIncomeCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Income category was not found.")));
        income.setDonorName(normalizeDonorName(income.getDonorName()));
        income.setAddUser(authenticatedUserService.getRequiredUserId());
        return incomeRepository.save(income);
    }

    private String normalizeDonorName(String donorName) {
        if (donorName == null || donorName.isBlank()) return null;
        String normalized = donorName.trim().replaceAll("\\s+", " ");
        if (normalized.length() > 150) {
            throw new IllegalArgumentException(
                    "Donor name must not exceed 150 characters.");
        }
        return normalized;
    }

    public List<TransactionIncome> getAllIncomes() {
        return incomeRepository.findAll();
    }

    public List<TransactionIncome> getIncomesByPool(Integer poolId) {
        return incomeRepository.findByFundingPoolId(poolId);
    }
}
