package com.pirivena_project.pirivena.controller;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import org.springframework.security.core.Authentication;
import com.pirivena_project.pirivena.modal.LibraryMember;
import com.pirivena_project.pirivena.service.LibraryMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/library-members") @RequiredArgsConstructor
public class LibraryMemberController { private final LibraryMemberService service; private final AssignmentSecurity assignmentSecurity; @GetMapping public List<LibraryMember> all(Authentication authentication){return assignmentSecurity.visibleLibraryMembers(service.getAll(), authentication);} @PostMapping public ResponseEntity<LibraryMember> create(@RequestBody LibraryMember member){return ResponseEntity.status(HttpStatus.CREATED).body(service.save(member));} @PutMapping("/{id}") public LibraryMember update(@PathVariable Integer id,@RequestBody LibraryMember member){member.setId(id);return service.save(member);} }
