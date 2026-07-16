package com.pirivena_project.pirivena.controller;
import com.pirivena_project.pirivena.modal.BookCategory;
import com.pirivena_project.pirivena.repository.BookCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/book-categories") @RequiredArgsConstructor
public class BookCategoryController { private final BookCategoryRepository repository;
 @GetMapping public List<BookCategory> getAll(){return repository.findAll();}
 @PostMapping public ResponseEntity<BookCategory> create(@RequestBody BookCategory category){if(category.getName()==null||category.getName().isBlank())throw new IllegalArgumentException("Category name is required.");if(repository.existsByNameIgnoreCase(category.getName()))throw new IllegalArgumentException("Category already exists.");return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(category));}}
