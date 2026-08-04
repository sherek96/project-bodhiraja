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
import com.pirivena_project.pirivena.service.ReferenceDataNameValidator;

@RestController
@RequestMapping("/api/income-categories")
@RequiredArgsConstructor
public class IncomeCategoryController {
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ReferenceDataNameValidator nameValidator;

    @GetMapping
    public ResponseEntity<List<IncomeCategory>> getAllIncomeCategories() {
        return ResponseEntity.ok(incomeCategoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<IncomeCategory> createIncomeCategory(
            @RequestBody IncomeCategory category) {
        String name = nameValidator.requiredName(category == null ? null : category.getName(), "Income category", 100);
        if (incomeCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("This income category already exists.");
        }
        category.setId(null);
        category.setName(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incomeCategoryRepository.save(category));
    }

}
