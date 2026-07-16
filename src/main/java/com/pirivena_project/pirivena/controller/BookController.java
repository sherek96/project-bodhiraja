package com.pirivena_project.pirivena.controller;
import com.pirivena_project.pirivena.modal.Book;
import com.pirivena_project.pirivena.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/books") @RequiredArgsConstructor
public class BookController { private final BookService service; @GetMapping public List<Book> all(){return service.getAll();} @PostMapping public ResponseEntity<Book> create(@RequestBody Book book){return ResponseEntity.status(HttpStatus.CREATED).body(service.save(book));} @PutMapping("/{id}") public Book update(@PathVariable Integer id,@RequestBody Book book){book.setId(id);return service.save(book);} @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Integer id){service.delete(id);} }
