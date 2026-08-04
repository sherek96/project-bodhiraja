package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.EventDetails;
import com.pirivena_project.pirivena.modal.FundingPool;
import com.pirivena_project.pirivena.modal.TransactionExpense;
import com.pirivena_project.pirivena.repository.EventDetailsRepository;
import com.pirivena_project.pirivena.repository.FundingPoolRepository;
import com.pirivena_project.pirivena.repository.TransactionExpenseRepository;
import com.pirivena_project.pirivena.repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionExpenseService {

    @Autowired
    private TransactionExpenseRepository expenseRepository;

    @Autowired
    private FundingPoolRepository poolRepository;

    @Autowired
    private EventDetailsRepository eventRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Transactional
    public TransactionExpense saveExpense(TransactionExpense expense) {
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero.");
        }
        if (expense.getDateSpent() == null || expense.getDescription() == null || expense.getDescription().isBlank() || expense.getExpenseCategory() == null || expense.getExpenseCategory().getId() == null) {
            throw new IllegalArgumentException("Expense date, description, and category are required.");
        }
        // --- 1. FIREWALL VETTING: FUNDING POOL CHECK ---
        if (expense.getFundingPool() == null || expense.getFundingPool().getId() == null) {
            throw new IllegalArgumentException("Expense must point to a source Funding Pool.");
        }

        FundingPool pool = poolRepository.findByIdForUpdate(expense.getFundingPool().getId())
                .orElseThrow(() -> new IllegalArgumentException("Source Funding Pool not found."));

        // Anti-Deficit Validation
        if (pool.getCurrentBalance().compareTo(expense.getAmount()) < 0) {
            throw new IllegalStateException("Transaction Denied: Insufficient balance in '" + pool.getName() + "'.");
        }

        // --- 2. FIREWALL VETTING: EVENT MANAGEMENT CHECK (IF ATTACHED) ---
        if (expense.getEvent() != null && expense.getEvent().getId() != null) {
            EventDetails event = eventRepository.findById(expense.getEvent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Linked Event details not found."));

            // Budget Overrun Validation Execution
            BigDecimal totalSpentSoFar = expenseRepository.getTotalSpentByEvent(event.getId());
            BigDecimal projectedTotal = totalSpentSoFar.add(expense.getAmount());

            BigDecimal budget = event.getBudgetAllocation() == null ? BigDecimal.ZERO : event.getBudgetAllocation();
            if (projectedTotal.compareTo(budget) > 0) {
                throw new IllegalStateException("Transaction Denied: This cost exceeds the allocated event budget limit of LKR " + budget);
            }

            expense.setEvent(event); // Reattach synchronized entity
        }

        // --- 3. EXECUTION: DEDUCT BALANCES AND COMMIT ---
        pool.setCurrentBalance(pool.getCurrentBalance().subtract(expense.getAmount()));
        poolRepository.save(pool);

        expense.setFundingPool(pool);
        expense.setExpenseCategory(expenseCategoryRepository.findById(expense.getExpenseCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Expense category was not found.")));
        expense.setAddUser(authenticatedUserService.getRequiredUserId());
        return expenseRepository.save(expense);
    }

    public List<TransactionExpense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<TransactionExpense> getExpensesByPool(Integer poolId) {
        return expenseRepository.findByFundingPoolId(poolId);
    }
}
