package com.riri.eventhop.events.controller;

import com.riri.eventhop.events.dto.CreateEventRequest;
import com.riri.eventhop.events.dto.EventDetailsResponse;
import com.riri.eventhop.events.entity.Event;
import com.riri.eventhop.events.service.EventService;
import com.riri.eventhop.response.Response;
import com.riri.eventhop.util.CustomPageable;
import com.riri.eventhop.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Response<Page<EventDetailsResponse>>> getAllUpcomingEvents(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventDetailsResponse> events = eventService.getAllUpcomingEvents(pageable);
        return Response.success("Upcoming events retrieved successfully", events);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Response<Page<EventDetailsResponse>>> getUpcomingEventsByCategory(
            @PathVariable String category,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventDetailsResponse> events = eventService.getUpcomingEventsByCategory(category, pageable);
        return Response.success("Upcoming events by category retrieved successfully", events);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<Response<Page<EventDetailsResponse>>> getUpcomingEventsByLocation(
            @PathVariable String location,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventDetailsResponse> events = eventService.getUpcomingEventsByLocation(location, pageable);
        return Response.success("Upcoming events by location retrieved successfully", events);
    }

    @GetMapping("/sorted")
    public ResponseEntity<Response<Page<EventDetailsResponse>>> getUpcomingEventsSortedByClosestTime(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventDetailsResponse> events = eventService.getUpcomingEventsSortedByClosestTime(pageable);
        return Response.success("Upcoming events sorted by closest time retrieved successfully", events);
    }

    @GetMapping("/search")
    public ResponseEntity<Response<Page<EventDetailsResponse>>> searchUpcomingEvents(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortBy) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
        Page<EventDetailsResponse> events = eventService.searchUpcomingEvents(keyword, pageable);
        return Response.success("Upcoming events search results retrieved successfully", events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<EventDetailsResponse>> getEventById(@PathVariable Long id) {
        EventDetailsResponse event = eventService.getEventById(id);
        return Response.success("Event retrieved successfully", event);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<EventDetailsResponse>> createEvent(@Valid @RequestBody CreateEventRequest createEventRequest,
                                                                      @RequestParam Long userId) {
        Event event = convertToEvent(createEventRequest);
        EventDetailsResponse createdEvent = eventService.createEvent(event, userId);
        return Response.success("Event created successfully", createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<EventDetailsResponse>> updateEvent(@PathVariable Long id,
                                                                      @Valid @RequestBody CreateEventRequest updateEventRequest) {
        Event event = convertToEvent(updateEventRequest);
        EventDetailsResponse updatedEvent = eventService.updateEvent(id, event);
        return Response.success("Event updated successfully", updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return Response.success("Event deleted successfully");
    }

    private Event convertToEvent(CreateEventRequest createEventRequest) {
        Event event = new Event();
        event.setTitle(createEventRequest.getTitle());
        event.setCode(createEventRequest.getCode());
        event.setEventCategory(createEventRequest.getEventCategory());
        event.setDescription(createEventRequest.getDescription());
        event.setMainImage(createEventRequest.getMainImage());
        event.setImageUrls(createEventRequest.getImageUrls());
        event.setAddress(createEventRequest.getAddress());
        event.setLocation(createEventRequest.getLocation());
        event.setStartTime(createEventRequest.getStartTime());
        event.setEndTime(createEventRequest.getEndTime());
        event.setPrice(createEventRequest.getPrice());
        event.setAvailableSeats(createEventRequest.getAvailableSeats());
        event.setUrl(createEventRequest.getUrl());
        return event;
    }
}