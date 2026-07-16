package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.EventCategory;
import com.pirivena_project.pirivena.repository.EventCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}
