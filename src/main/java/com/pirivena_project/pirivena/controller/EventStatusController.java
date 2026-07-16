package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.EventStatus;
import com.pirivena_project.pirivena.repository.EventStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-statuses")
public class EventStatusController {

    @Autowired
    private EventStatusRepository statusRepository;

    @GetMapping
    public ResponseEntity<List<EventStatus>> getAllStatuses() {
        return ResponseEntity.ok(statusRepository.findAll());
    }
}
