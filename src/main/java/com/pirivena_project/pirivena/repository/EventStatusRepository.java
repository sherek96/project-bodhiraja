package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EventStatusRepository extends JpaRepository<EventStatus, Integer> {
    // Used by the service layer to auto-assign default lifecycle states
    Optional<EventStatus> findByName(String name);
}