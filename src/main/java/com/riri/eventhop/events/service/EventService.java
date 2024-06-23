package com.riri.eventhop.events.service;

import com.riri.eventhop.DashboardDTO;
import com.riri.eventhop.events.dto.CreateEventRequest;
import com.riri.eventhop.events.dto.EventDTO;
import com.riri.eventhop.events.dto.EventSummaryDTO;
import com.riri.eventhop.events.dto.UpdateEventRequest;
import com.riri.eventhop.events.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {
    EventDTO createEvent(CreateEventRequest request, Long userId, List<String> imageUrls);
    Page<EventSummaryDTO> getEventsByOrganizer(Long userId, Pageable pageable);
    EventDTO getEventById(Long id);
    Page<EventSummaryDTO> getAllEvents(Pageable pageable);
    Page<EventSummaryDTO> getEventsByCategory(Category category, Pageable pageable);
    Page<EventSummaryDTO> getEventsByLocation(String location, Pageable pageable);
    Page<EventSummaryDTO> searchEvents(String keyword, Pageable pageable);
    EventDTO updateEvent(Long id, UpdateEventRequest request);
    void deleteEvent(Long id);
    DashboardDTO getOrganizerDashboard(Long userId);
}