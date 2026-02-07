package com.bodhiraja.bodhiraja.event.dao;

import com.bodhiraja.bodhiraja.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventStatusRepository extends JpaRepository<EventStatus, Integer> {
    // Useful helper to find a status object by its name
    EventStatus findByName(String name);
}