package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.TransactionExpense;
import com.pirivena_project.pirivena.service.TransactionExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class TransactionExpenseController {

    @Autowired
    private TransactionExpenseService expenseService;

    @PostMapping
    public ResponseEntity<TransactionExpense> addExpense(@RequestBody TransactionExpense expense) {
        TransactionExpense savedExpense = expenseService.saveExpense(expense);
        return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionExpense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/pool/{poolId}")
    public ResponseEntity<List<TransactionExpense>> getExpensesByPool(@PathVariable Integer poolId) {
        return ResponseEntity.ok(expenseService.getExpensesByPool(poolId));
    }
}
