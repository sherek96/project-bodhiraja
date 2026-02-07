package com.bodhiraja.bodhiraja.finance.controller;

import com.bodhiraja.bodhiraja.finance.*; // Imports Donor, ItemDonation, Income, Expense
import com.bodhiraja.bodhiraja.finance.dao.*; // Imports all Repos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/finance")
public class FinanceController {

    // --- 1. Donors (People who give money/items) ---
    @Autowired
    private DonorRepository donorDao;

    @GetMapping(value = "/donor/all", produces = "application/json")
    public List<Donor> getAllDonors() {
        return donorDao.findAll();
    }

    // --- 2. Item Donations (Rice, Books, etc.) ---
    @Autowired
    private ItemDonationRepository itemDonationDao;

    @GetMapping(value = "/itemdonation/all", produces = "application/json")
    public List<ItemDonation> getAllItemDonations() {
        return itemDonationDao.findAll();
    }

    // --- 3. Income (Cash In: Grants, Donations) ---
    @Autowired
    private TransactionIncomeRepository incomeDao;

    @GetMapping(value = "/income/all", produces = "application/json")
    public List<TransactionIncome> getAllIncome() {
        return incomeDao.findAll();
    }

    // --- 4. Expenses (Cash Out: Bills, Events) ---
    @Autowired
    private TransactionExpenseRepository expenseDao;

    @GetMapping(value = "/expense/all", produces = "application/json")
    public List<TransactionExpense> getAllExpenses() {
        return expenseDao.findAll();
    }
}