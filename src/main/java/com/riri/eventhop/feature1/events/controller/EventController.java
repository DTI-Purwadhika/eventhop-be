package com.riri.eventhop.feature1.events.controller;

import com.riri.eventhop.feature1.events.dto.EventDetailsRequest;
import com.riri.eventhop.feature1.events.dto.EventDetailsResponse;
import com.riri.eventhop.feature1.events.dto.EventSummaryResponse;
import com.riri.eventhop.feature1.events.service.EventService;
import com.riri.eventhop.response.Response;
import com.riri.eventhop.util.CustomPageable;
import com.riri.eventhop.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Response<Page<EventSummaryResponse>>> getAllUpcomingEvents(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventSummaryResponse> events = eventService.getAllUpcomingEvents(pageable);
        return Response.success("Upcoming events retrieved successfully", events);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Response<Page<EventSummaryResponse>>> getUpcomingEventsByCategory(
            @PathVariable String category,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventSummaryResponse> events = eventService.getUpcomingEventsByCategory(category, pageable);
        return Response.success("Upcoming events by category retrieved successfully", events);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<Response<Page<EventSummaryResponse>>> getUpcomingEventsByLocation(
            @PathVariable String location,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventSummaryResponse> events = eventService.getUpcomingEventsByLocation(location, pageable);
        return Response.success("Upcoming events by location retrieved successfully", events);
    }

    @GetMapping("/sorted")
    public ResponseEntity<Response<Page<EventSummaryResponse>>> getUpcomingEventsSortedByClosestTime(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventSummaryResponse> events = eventService.getUpcomingEventsSortedByClosestTime(pageable);
        return Response.success("Upcoming events sorted by closest time retrieved successfully", events);
    }

    @GetMapping("/search")
    public ResponseEntity<Response<Page<EventSummaryResponse>>> searchUpcomingEvents(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventSummaryResponse> events = eventService.searchUpcomingEvents(keyword, pageable);
        return Response.success("Upcoming events search results retrieved successfully", events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<EventDetailsResponse>> getEventById(@PathVariable Long id) {
        EventDetailsResponse event = eventService.getEventById(id);
        return Response.success("Event retrieved successfully", event);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<EventDetailsResponse>> createEvent(@Valid @RequestBody EventDetailsRequest eventDetailsRequest) {
        EventDetailsResponse createdEvent = eventService.createEvent(eventDetailsRequest);
        return Response.success("Event created successfully", createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<EventDetailsResponse>> updateEvent(@PathVariable Long id,
                                                                      @Valid @RequestBody EventDetailsRequest eventDetailsRequest) {
        EventDetailsResponse updatedEvent = eventService.updateEvent(id, eventDetailsRequest);
        return Response.success("Event updated successfully", updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return Response.success("Event deleted successfully");
    }
}
