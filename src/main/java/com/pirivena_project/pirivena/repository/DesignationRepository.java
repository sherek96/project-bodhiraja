package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes designation records in the database.

import com.pirivena_project.pirivena.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<Designation, Integer> {

}
