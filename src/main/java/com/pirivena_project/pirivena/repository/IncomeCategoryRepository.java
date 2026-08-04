package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Integer> {
    boolean existsByNameIgnoreCase(String name);
    java.util.Optional<IncomeCategory> findByNameIgnoreCase(String name);
}
