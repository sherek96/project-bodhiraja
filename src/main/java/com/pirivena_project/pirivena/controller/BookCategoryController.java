package com.pirivena_project.pirivena.controller;

// Purpose: Exposes HTTP endpoints for book category operations.
import com.pirivena_project.pirivena.model.BookCategory;
import com.pirivena_project.pirivena.repository.BookCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.pirivena_project.pirivena.service.ReferenceDataNameValidator;
@RestController @RequestMapping("/api/book-categories") @RequiredArgsConstructor
public class BookCategoryController { private final BookCategoryRepository repository; private final ReferenceDataNameValidator nameValidator;
 @GetMapping public List<BookCategory> getAll(){return repository.findAll();}
 @PostMapping public ResponseEntity<BookCategory> create(@RequestBody BookCategory category){String name=nameValidator.requiredName(category==null?null:category.getName(),"Book category",100);if(repository.existsByNameIgnoreCase(name))throw new IllegalArgumentException("Category already exists.");category.setId(null);category.setName(name);return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(category));}}
