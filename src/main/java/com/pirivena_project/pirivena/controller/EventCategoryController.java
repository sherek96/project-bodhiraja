package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.EventCategory;
import com.pirivena_project.pirivena.repository.EventCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-categories")
public class EventCategoryController {

    @Autowired
    private EventCategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<EventCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<EventCategory> createCategory(
            @RequestBody EventCategory category) {
        if (category == null || category.getName() == null
                || category.getName().isBlank()) {
            throw new IllegalArgumentException("Event category name is required.");
        }

        String normalizedName = category.getName().trim()
                .replaceAll("\\s+", " ");
        if (normalizedName.length() > 45) {
            throw new IllegalArgumentException(
                    "Event category name must not exceed 45 characters.");
        }
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
