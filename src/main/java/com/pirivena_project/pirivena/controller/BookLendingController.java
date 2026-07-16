package com.pirivena_project.pirivena.controller;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import org.springframework.security.core.Authentication;
import com.pirivena_project.pirivena.dto.ReturnBookRequest;
import com.pirivena_project.pirivena.modal.BookLending;
import com.pirivena_project.pirivena.service.BookLendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/book-lendings") @RequiredArgsConstructor
public class BookLendingController { private final BookLendingService service; private final AssignmentSecurity assignmentSecurity; @GetMapping public List<BookLending> all(Authentication authentication){return assignmentSecurity.visibleBookLendings(service.getAll(), authentication);} @PostMapping public ResponseEntity<BookLending> issue(@RequestBody BookLending lending){return ResponseEntity.status(HttpStatus.CREATED).body(service.issue(lending));} @PostMapping("/{id}/return") public BookLending returnBook(@PathVariable Integer id,@RequestBody ReturnBookRequest request){return service.returnBook(id,request);} }
