package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.ExpenseCategory;
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

@RestController
@RequestMapping("/api/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @GetMapping
    public ResponseEntity<List<ExpenseCategory>> getAllExpenseCategories() {
        return ResponseEntity.ok(expenseCategoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<ExpenseCategory> createExpenseCategory(
            @RequestBody ExpenseCategory category) {
        String name = normalizeName(category == null ? null : category.getName());
        if (expenseCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("This expense category already exists.");
        }
        category.setId(null);
        category.setName(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseCategoryRepository.save(category));
    }

    private String normalizeName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Expense category name is required.");
        }
        String name = value.trim().replaceAll("\\s+", " ");
        if (name.length() > 100) {
            throw new IllegalArgumentException(
                    "Expense category name must not exceed 100 characters.");
        }
        return name;
    }
}
