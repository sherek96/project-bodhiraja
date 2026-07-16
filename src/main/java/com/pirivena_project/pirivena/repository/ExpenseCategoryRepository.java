package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Integer> {
}
