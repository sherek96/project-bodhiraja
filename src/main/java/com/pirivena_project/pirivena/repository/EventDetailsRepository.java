package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.EventDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDetailsRepository extends JpaRepository<EventDetails, Integer> {
}