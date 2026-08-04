package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.IncomeCategory;
import com.pirivena_project.pirivena.repository.IncomeCategoryRepository;
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
@RequestMapping("/api/income-categories")
@RequiredArgsConstructor
public class IncomeCategoryController {
    private final IncomeCategoryRepository incomeCategoryRepository;

    @GetMapping
    public ResponseEntity<List<IncomeCategory>> getAllIncomeCategories() {
        return ResponseEntity.ok(incomeCategoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<IncomeCategory> createIncomeCategory(
            @RequestBody IncomeCategory category) {
        String name = normalizeName(category == null ? null : category.getName());
        if (incomeCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("This income category already exists.");
        }
        category.setId(null);
        category.setName(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incomeCategoryRepository.save(category));
    }

    private String normalizeName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Income category name is required.");
        }
        String name = value.trim().replaceAll("\\s+", " ");
        if (name.length() > 100) {
            throw new IllegalArgumentException(
                    "Income category name must not exceed 100 characters.");
        }
        return name;
    }
}
