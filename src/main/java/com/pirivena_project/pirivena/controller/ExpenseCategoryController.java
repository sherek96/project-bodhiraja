package com.pirivena_project.pirivena.controller;

// Purpose: Exposes HTTP endpoints for expense category operations.

import com.pirivena_project.pirivena.model.ExpenseCategory;
import com.pirivena_project.pirivena.repository.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.pirivena_project.pirivena.service.ReferenceDataNameValidator;

@RestController
@RequestMapping("/api/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ReferenceDataNameValidator nameValidator;

    @GetMapping
    public ResponseEntity<List<ExpenseCategory>> getAllExpenseCategories() {
        return ResponseEntity.ok(expenseCategoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<ExpenseCategory> createExpenseCategory(
            @RequestBody ExpenseCategory category) {
        String name = nameValidator.requiredName(category == null ? null : category.getName(), "Expense category", 100);
        if (expenseCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("This expense category already exists.");
        }
        category.setId(null);
        category.setName(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseCategoryRepository.save(category));
    }

}
