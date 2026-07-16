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
import java.util.Map;
import com.pirivena_project.pirivena.dto.GuardianStudentSummaryDTO;
import com.pirivena_project.pirivena.dto.GuardianDeactivationRequest;
import com.pirivena_project.pirivena.dto.GuardianResponseDTO;
import com.pirivena_project.pirivena.enums.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/guardians")
public class GuardianController {

    @Autowired
    private GuardianService guardianService;
    @Autowired
    private AssignmentSecurity assignmentSecurity;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(defaultValue = "") String search, @RequestParam(required = false) GuardianStatus status,
            @RequestParam(required = false) GuardianRelationship relationship, @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Boolean multipleStudents, @RequestParam(required = false) Boolean missingContact,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "fullName,asc") String sort,
            Authentication authentication) {
        List<GuardianResponseDTO> filtered = filteredResponses(search, status, relationship, gender, multipleStudents, missingContact, sort, authentication);
        int safeSize = Math.max(1, Math.min(size, 100)), safePage = Math.max(0, page);
        int from = Math.min(safePage * safeSize, filtered.size()), to = Math.min(from + safeSize, filtered.size());
        long active = filtered.stream().filter(g -> g.status() == GuardianStatus.ACTIVE).count();
        return ResponseEntity.ok(Map.of("content", filtered.subList(from, to), "totalElements", filtered.size(),
                "totalPages", (filtered.size() + safeSize - 1) / safeSize, "number", safePage, "size", safeSize,
                "summary", Map.of("active", active, "inactive", filtered.size() - active)));
    }

    @GetMapping("/report")
    public ResponseEntity<List<GuardianResponseDTO>> report(
            @RequestParam(defaultValue = "") String search, @RequestParam(required = false) GuardianStatus status,
            @RequestParam(required = false) GuardianRelationship relationship, @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Boolean multipleStudents, @RequestParam(required = false) Boolean missingContact,
            @RequestParam(defaultValue = "fullName,asc") String sort,
            Authentication authentication) {
        return ResponseEntity.ok(filteredResponses(search, status, relationship, gender, multipleStudents, missingContact, sort, authentication));
    }

    private List<GuardianResponseDTO> filteredResponses(String search, GuardianStatus status, GuardianRelationship relationship,
            Gender gender, Boolean multipleStudents, Boolean missingContact, String sort, Authentication authentication) {
        String[] parts = sort.split(",", 2);
        boolean descending = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]);
        boolean reveal = assignmentSecurity.canViewSensitiveGuardianData(authentication);
        var visible = assignmentSecurity.visibleGuardians(guardianService.getAllGuardians(), authentication);
        return guardianService.filterAndSortGuardians(visible, search, status, gender, relationship, multipleStudents, missingContact,
                parts[0], descending).stream().map(g -> guardianService.toResponse(g, reveal)).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.guardianVisible(#id, authentication)")
    public ResponseEntity<?> getGuardianById(@PathVariable Integer id, Authentication authentication) {
        try{
            Guardian guardian = guardianService.getGuardianById(id);
            boolean reveal = assignmentSecurity.canViewSensitiveGuardianData(authentication);
            if (reveal) guardianService.auditAccess(id, authentication.getName(), "VIEW");
            return new ResponseEntity<>(guardianService.toResponse(guardian, reveal), HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("@assignmentSecurity.guardianVisible(#id, authentication)")
    public ResponseEntity<List<GuardianStudentSummaryDTO>> getLinkedStudents(@PathVariable Integer id) {
        return ResponseEntity.ok(guardianService.getLinkedStudents(id));
    }

    @GetMapping("/relationship-summary")
    public ResponseEntity<Map<String, Long>> getRelationshipSummary(Authentication authentication) {
        List<Integer> visibleIds = assignmentSecurity.visibleGuardians(guardianService.getAllGuardians(), authentication)
                .stream().map(Guardian::getId).toList();
        return ResponseEntity.ok(guardianService.getRelationshipSummary(visibleIds));
    }
    @PostMapping
    @PreAuthorize("@assignmentSecurity.canViewSensitiveGuardianData(authentication)")
    public ResponseEntity<?> create(@RequestBody Guardian guardian, Authentication authentication) {
        try {
            Guardian savedGuardian = guardianService.createGuardian(guardian);
            guardianService.auditAccess(savedGuardian.getId(), authentication.getName(), "CREATE");
            return new ResponseEntity<>(guardianService.toResponse(savedGuardian, true), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Catches validation messages like "NIC already exists"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Unexpected error processing operation: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.canViewSensitiveGuardianData(authentication)")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Guardian guardian, Authentication authentication) {
        try {
            guardian.setId(id);
            Guardian savedGuardian = guardianService.updateGuardian(guardian);
            guardianService.auditAccess(id, authentication.getName(), "UPDATE");
            return new ResponseEntity<>(guardianService.toResponse(savedGuardian, true), HttpStatus.OK);
        } catch (RuntimeException e) {
            // Catches validation or "Guardian not found" exceptions
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Unexpected error updating entity: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@assignmentSecurity.canViewSensitiveGuardianData(authentication)")
    public ResponseEntity<?> deleteGuardian(@PathVariable Integer id, Authentication authentication) {
        try {
            Guardian deletedGuardian = guardianService.deleteGuardian(id);
            guardianService.auditAccess(id, authentication.getName(), "DEACTIVATE");
            return new ResponseEntity<>(guardianService.toResponse(deletedGuardian, true), HttpStatus.OK);
        } catch (RuntimeException e) {
            // Handles missing record gracefully if 404 target is requested
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error deleting record: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("@assignmentSecurity.canViewSensitiveGuardianData(authentication)")
    public ResponseEntity<?> deactivateGuardian(@PathVariable Integer id, @RequestBody(required = false) GuardianDeactivationRequest request, Authentication authentication) {
        try {
            Guardian guardian = guardianService.deactivateGuardian(id, request);
            guardianService.auditAccess(id, authentication.getName(), "DEACTIVATE");
            return ResponseEntity.ok(guardianService.toResponse(guardian, true));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/profile-picture")
    @PreAuthorize("@assignmentSecurity.canViewSensitiveGuardianData(authentication)")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(guardianService.uploadProfilePicture(id, file));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
