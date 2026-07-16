package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.EventDetails;
import com.pirivena_project.pirivena.service.EventDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventDetailsController {

    @Autowired
    private EventDetailsService eventService;

    @PostMapping
    public ResponseEntity<EventDetails> createEvent(@RequestBody EventDetails event) {
        EventDetails savedEvent = eventService.createEvent(event);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventDetails>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDetails> getEventById(@PathVariable Integer id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // Endpoint to shift state e.g., transitioning an event to 'Completed' or 'Cancelled'
    @PutMapping("/{id}/status/{statusId}")
    public ResponseEntity<EventDetails> changeEventStatus(@PathVariable Integer id, @PathVariable Integer statusId) {
        EventDetails modifiedEvent = eventService.updateEventStatus(id, statusId);
        return ResponseEntity.ok(modifiedEvent);
    }
}
