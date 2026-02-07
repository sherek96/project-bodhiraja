package com.bodhiraja.bodhiraja.event.dao;

import com.bodhiraja.bodhiraja.event.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
}