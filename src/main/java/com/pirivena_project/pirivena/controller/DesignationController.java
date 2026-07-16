package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.Designation;
import com.pirivena_project.pirivena.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/designations")
public class DesignationController {

    @Autowired
    private DesignationService designationService;

    // React will trigger this immediately upon loading the registration form view
    @GetMapping
    public ResponseEntity<List<Designation>> getAllDesignations() {
        List<Designation> designations = designationService.getAllDesignations();
        return ResponseEntity.ok(designations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Designation> getDesignationById(@PathVariable Integer id) {
        Designation designation = designationService.getDesignationById(id);
        return ResponseEntity.ok(designation);
    }

    @PostMapping
    public ResponseEntity<?> createDesignation(@RequestBody Designation designation) {
        try {
            Designation savedDesignation = designationService.createDesignation(designation);
            return ResponseEntity.ok(savedDesignation);
        } catch (Exception e) {
            // Catches database runtime exceptions (like duplicate names or null constraints) smoothly
            return ResponseEntity.badRequest().body("Error saving designation: " + e.getMessage());
        }
    }
}
