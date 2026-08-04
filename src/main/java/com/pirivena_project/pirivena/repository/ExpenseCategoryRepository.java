package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes expense category records in the database.

import com.pirivena_project.pirivena.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Integer> {
    boolean existsByNameIgnoreCase(String name);
}
