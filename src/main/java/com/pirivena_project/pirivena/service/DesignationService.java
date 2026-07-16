package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.Designation;
import com.pirivena_project.pirivena.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    // Fetches all designations to populate your React frontend dropdown array
    public List<Designation> getAllDesignations() {
        return designationRepository.findAll();
    }

    // Fetches a single designation profile by its ID
    public Designation getDesignationById(Integer id) {
        return designationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Designation not found with ID: " + id));
    }

    // Saves a new designation record directly to the database
    public Designation createDesignation(Designation designation) {
        // The database `@Column(unique = true)` constraint will automatically protect against duplicate names
        return designationRepository.save(designation);
    }
}