package com.riri.eventhop.events.service;

import com.riri.eventhop.events.dto.*;
import com.riri.eventhop.events.entity.Event;
import com.riri.eventhop.events.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {
    List<Event> getEvents(String filter, String category, int limit, int page);
    EventDTO createEvent(CreateEventRequest request, String clerkId, List<String> imageUrls);
    Page<EventSummaryDTO> getEventsByOrganizer(Long userId, Pageable pageable);
    EventDTO getEventById(Long id);
    Page<EventSummaryDTO> getAllEvents(Pageable pageable);
    Page<EventSummaryDTO> getEventsByCategory(Category category, Pageable pageable);
    Page<EventSummaryDTO> getEventsByLocation(String location, Pageable pageable);
    Page<EventSummaryDTO> searchEvents(String keyword, Pageable pageable);
    EventDTO updateEvent(Long id, UpdateEventRequest request);
    void deleteEvent(Long id);
//    DashboardDTO getOrganizerDashboard(Long userId);
}