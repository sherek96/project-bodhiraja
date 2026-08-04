package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes income category records in the database.

import com.pirivena_project.pirivena.model.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Integer> {
    boolean existsByNameIgnoreCase(String name);
    java.util.Optional<IncomeCategory> findByNameIgnoreCase(String name);
}
