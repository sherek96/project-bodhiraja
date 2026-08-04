package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.EventDetails;
import com.pirivena_project.pirivena.repository.EventDetailsRepository;
import com.pirivena_project.pirivena.repository.EventCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

import java.util.List;

@Service
public class EventDetailsService {

    @Autowired
    private EventDetailsRepository eventRepository;

    @Autowired
    private EventCategoryRepository categoryRepository;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Transactional
    public EventDetails createEvent(EventDetails event) {
        if (event.getName() == null || event.getName().isBlank() || event.getStartDate() == null || event.getEventCategory() == null || event.getEventCategory().getId() == null) {
            throw new IllegalArgumentException("Event name, start date, and category are required.");
        }
        if (event.getEndDate() != null && event.getEndDate().isBefore(event.getStartDate())) {
            throw new IllegalArgumentException("Event end date cannot be before its start date.");
        }
        if (event.getBudgetAllocation() == null) event.setBudgetAllocation(BigDecimal.ZERO);
        if (event.getBudgetAllocation().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Event budget cannot be negative.");
        }
        event.setEventCategory(categoryRepository.findById(event.getEventCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Event category was not found.")));
        event.setLegacyEventStatusId(null);
        event.setAddUser(authenticatedUserService.getRequiredUserId());
        return eventRepository.save(event);
    }

    public List<EventDetails> getAllEvents() {
        return eventRepository.findAll();
    }

    public EventDetails getEventById(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event file not found with ID: " + id));
    }
}
