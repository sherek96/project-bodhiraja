package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes event details records in the database.

import com.pirivena_project.pirivena.model.EventDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDetailsRepository extends JpaRepository<EventDetails, Integer> {
}
