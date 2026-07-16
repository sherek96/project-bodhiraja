package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.Guardian;
import com.pirivena_project.pirivena.service.GuardianService;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/guardians")
public class GuardianController {

    @Autowired
    private GuardianService guardianService;
    @Autowired
    private AssignmentSecurity assignmentSecurity;

    @GetMapping
    public ResponseEntity<List<Guardian>> findAll(Authentication authentication) {
        return ResponseEntity.ok(assignmentSecurity.visibleGuardians(guardianService.getAllGuardians(), authentication));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.guardianVisible(#id, authentication)")
    public ResponseEntity<?> getGuardianById(@PathVariable Integer id) {
        try{
            Guardian guardian = guardianService.getGuardianById(id);
            return new ResponseEntity<>(guardian, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Guardian guardian) {
        try {
            Guardian savedGuardian = guardianService.createGuardian(guardian);
            return new ResponseEntity<>(savedGuardian, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Catches validation messages like "NIC already exists"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error processing operation: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.guardianVisible(#id, authentication)")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Guardian guardian) {
        try {
            guardian.setId(id);
            Guardian savedGuardian = guardianService.updateGuardian(guardian);
            return new ResponseEntity<>(savedGuardian, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Catches validation or "Guardian not found" exceptions
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error updating entity: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.guardianVisible(#id, authentication)")
    public ResponseEntity<?> deleteGuardian(@PathVariable Integer id) {
        try {
            Guardian deletedGuardian = guardianService.deleteGuardian(id);
            return new ResponseEntity<>(deletedGuardian, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Handles missing record gracefully if 404 target is requested
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error deleting record: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/profile-picture")
    @PreAuthorize("@assignmentSecurity.guardianVisible(#id, authentication)")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(guardianService.uploadProfilePicture(id, file));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
