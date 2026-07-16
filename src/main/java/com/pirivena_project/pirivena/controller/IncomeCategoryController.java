package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.IncomeCategory;
import com.pirivena_project.pirivena.repository.IncomeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
