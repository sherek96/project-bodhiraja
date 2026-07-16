package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.EventDetails;
import com.pirivena_project.pirivena.modal.EventStatus;
import com.pirivena_project.pirivena.repository.EventDetailsRepository;
import com.pirivena_project.pirivena.repository.EventStatusRepository;
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
    private EventStatusRepository statusRepository;

    @Autowired
    private EventCategoryRepository categoryRepository;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Transactional
    public EventDetails createEvent(EventDetails event) {
        if (event.getName() == null || event.getName().isBlank() || event.getStartDate() == null || event.getEventCategory() == null || event.getEventCategory().getId() == null) {
            throw new IllegalArgumentException("Event name, start date, and category are required.");
        }
        if (event.getEndDate() != null && !event.getEndDate().isAfter(event.getStartDate())) {
            throw new IllegalArgumentException("Event end date must be later than its start date.");
        }
        if (event.getBudgetAllocation() == null) event.setBudgetAllocation(BigDecimal.ZERO);
        if (event.getBudgetAllocation().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Event budget cannot be negative.");
        }
        event.setEventCategory(categoryRepository.findById(event.getEventCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Event category was not found.")));
        // Automatically default new event records to "Scheduled" if no status is chosen
        if (event.getEventStatus() == null || event.getEventStatus().getId() == null) {
            EventStatus defaultStatus = statusRepository.findByName("Scheduled")
                    .orElseThrow(() -> new IllegalStateException(
                            "System configuration missing: 'Scheduled' status must be seeded in the database."));
            event.setEventStatus(defaultStatus);
        }
        event.setAddUser(authenticatedUserService.getRequiredUserId());
        return eventRepository.save(event);
    }

    @Transactional
    public EventDetails updateEventStatus(Integer eventId, Integer statusId) {
        EventDetails event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event file not found with ID: " + eventId));

        EventStatus targetStatus = statusRepository.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Target Event Status not found with ID: " + statusId));

        event.setEventStatus(targetStatus);
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
