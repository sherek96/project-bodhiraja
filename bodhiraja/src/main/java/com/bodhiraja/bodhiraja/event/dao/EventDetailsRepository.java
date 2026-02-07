package com.bodhiraja.bodhiraja.event.dao;

import com.bodhiraja.bodhiraja.event.EventDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventDetailsRepository extends JpaRepository<EventDetails, Integer> {

    // 1. Find all events with a specific status (e.g., "Scheduled")
    // Note: We use the underscore '_' to dig into the related 'EventStatus' object and check its 'name'
    List<EventDetails> findByEventStatus_Name(String statusName);

    // 2. Find events happening between two dates (Useful for "Events this Month")
    List<EventDetails> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
}