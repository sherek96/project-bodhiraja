package com.bodhiraja.bodhiraja.event.controller;

import com.bodhiraja.bodhiraja.event.*; // Imports EventDetails, EventCategory, EventStatus
import com.bodhiraja.bodhiraja.event.dao.*; // Imports Repositories
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/event")
public class EventController {

    // --- 1. Main Event List (The Calendar) ---
    @Autowired
    private EventDetailsRepository eventDetailsDao;

    @GetMapping(value = "/all", produces = "application/json")
    public List<EventDetails> getAllEvents() {
        return eventDetailsDao.findAll();
    }

    // --- 2. Event Categories (Religious, Sports, etc.) ---
    @Autowired
    private EventCategoryRepository eventCategoryDao;

    @GetMapping(value = "/category/all", produces = "application/json")
    public List<EventCategory> getAllCategories() {
        return eventCategoryDao.findAll();
    }

    // --- 3. Event Statuses (Scheduled, Completed, Cancelled) ---
    @Autowired
    private EventStatusRepository eventStatusDao;

    @GetMapping(value = "/status/all", produces = "application/json")
    public List<EventStatus> getAllStatuses() {
        return eventStatusDao.findAll();
    }
}