package com.riri.eventhop.events.service;

import com.riri.eventhop.events.dto.EventDetailsResponse;
import com.riri.eventhop.events.entity.Event;
import com.riri.eventhop.util.CustomPageable;
import org.springframework.data.domain.Page;

public interface EventService {
    Page<EventDetailsResponse> getAllUpcomingEvents(CustomPageable pageable);
    Page<EventDetailsResponse> getUpcomingEventsByCategory(String category, CustomPageable pageable);
    Page<EventDetailsResponse> getUpcomingEventsByLocation(String location, CustomPageable pageable);
    Page<EventDetailsResponse> getUpcomingEventsSortedByClosestTime(CustomPageable pageable);
    Page<EventDetailsResponse> searchUpcomingEvents(String keyword, CustomPageable pageable);
    EventDetailsResponse getEventById(Long id);
    EventDetailsResponse createEvent(Event event, Long userId);
    EventDetailsResponse updateEvent(Long id, Event updatedEvent);
    void deleteEvent(Long id);
    EventDetailsResponse mapEventToEventResponse(Event event);
}