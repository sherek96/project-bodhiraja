package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes event category records in the database.

import com.pirivena_project.pirivena.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
    boolean existsByNameIgnoreCase(String name);
}
