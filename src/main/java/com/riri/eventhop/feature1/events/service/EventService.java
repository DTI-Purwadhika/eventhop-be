package com.riri.eventhop.feature1.events.service;

import com.riri.eventhop.feature1.events.dto.EventDetailsRequest;
import com.riri.eventhop.feature1.events.dto.EventDetailsResponse;
import com.riri.eventhop.feature1.events.dto.EventSummaryResponse;
import com.riri.eventhop.util.CustomPageable;
import org.springframework.data.domain.Page;

public interface EventService {

    Page<EventSummaryResponse> getAllUpcomingEvents(CustomPageable pageable);

    Page<EventSummaryResponse> getUpcomingEventsByCategory(String category, CustomPageable pageable);

    Page<EventSummaryResponse> getUpcomingEventsByLocation(String location, CustomPageable pageable);

    Page<EventSummaryResponse> getUpcomingEventsSortedByClosestTime(CustomPageable pageable);

    Page<EventSummaryResponse> searchUpcomingEvents(String keyword, CustomPageable pageable);

    EventDetailsResponse getEventById(Long id);

    EventDetailsResponse createEvent(EventDetailsRequest eventDetailsRequest);

    EventDetailsResponse updateEvent(Long id, EventDetailsRequest eventDetailsRequest);

    void deleteEvent(Long id);
}
