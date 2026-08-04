package com.pirivena_project.pirivena.controller;

// Purpose: Exposes HTTP endpoints for event category operations.

import com.pirivena_project.pirivena.model.EventCategory;
import com.pirivena_project.pirivena.repository.EventCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.pirivena_project.pirivena.service.ReferenceDataNameValidator;

@RestController
@RequestMapping("/api/event-categories")
@RequiredArgsConstructor
public class EventCategoryController {

    private final EventCategoryRepository categoryRepository;
    private final ReferenceDataNameValidator nameValidator;

    @GetMapping
    public ResponseEntity<List<EventCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<EventCategory> createCategory(
            @RequestBody EventCategory category) {
        String normalizedName = nameValidator.requiredName(
                category == null ? null : category.getName(), "Event category", 45);
        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException(
                    "An event category with this name already exists.");
        }

        category.setId(null);
        category.setName(normalizedName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryRepository.save(category));
    }
}
