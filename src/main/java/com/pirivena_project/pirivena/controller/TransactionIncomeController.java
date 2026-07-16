package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.TransactionIncome;
import com.pirivena_project.pirivena.service.TransactionIncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
public class TransactionIncomeController {

    @Autowired
    private TransactionIncomeService incomeService;

    @PostMapping
    public ResponseEntity<TransactionIncome> addIncome(@RequestBody TransactionIncome income) {
        TransactionIncome savedIncome = incomeService.saveIncome(income);
        return new ResponseEntity<>(savedIncome, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionIncome>> getAllIncomes() {
        return ResponseEntity.ok(incomeService.getAllIncomes());
    }

    @GetMapping("/pool/{poolId}")
    public ResponseEntity<List<TransactionIncome>> getIncomesByPool(@PathVariable Integer poolId) {
        return ResponseEntity.ok(incomeService.getIncomesByPool(poolId));
    }
}
