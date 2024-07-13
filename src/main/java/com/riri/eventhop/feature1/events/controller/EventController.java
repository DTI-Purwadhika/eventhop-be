package com.riri.eventhop.feature1.events.controller;

import com.riri.eventhop.feature1.events.dto.EventDetailsRequest;
import com.riri.eventhop.feature1.events.dto.EventDetailsResponse;
import com.riri.eventhop.feature1.events.dto.EventSummaryResponse;
import com.riri.eventhop.feature1.events.dto.GetAllEventsParams;
import com.riri.eventhop.feature1.events.entity.EventCategory;
import com.riri.eventhop.feature1.events.service.EventService;
import com.riri.eventhop.response.Response;
import com.riri.eventhop.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

//    @GetMapping
//    public ResponseEntity<Response<Page<EventSummaryResponse>>> getAllUpcomingEvents(
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer limit,
//            @RequestParam(required = false) String sortDirection,
//            @RequestParam(required = false) String sortBy) {
//        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
//        Page<EventSummaryResponse> events = eventService.getAllUpcomingEvents(pageable);
//        return Response.success("Upcoming events retrieved successfully", events);
//    }
//
//    @GetMapping("/category/{category}")
//    public ResponseEntity<Response<Page<EventSummaryResponse>>> getUpcomingEventsByCategory(
//            @PathVariable String category,
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer limit,
//            @RequestParam(required = false) String sortDirection,
//            @RequestParam(required = false) String sortBy) {
//        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
//        Page<EventSummaryResponse> events = eventService.getUpcomingEventsByCategory(category, pageable);
//        return Response.success("Upcoming events by category retrieved successfully", events);
//    }
//
//    @GetMapping("/location/{location}")
//    public ResponseEntity<Response<Page<EventSummaryResponse>>> getUpcomingEventsByLocation(
//            @PathVariable String location,
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer limit,
//            @RequestParam(required = false) String sortDirection,
//            @RequestParam(required = false) String sortBy) {
//        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
//        Page<EventSummaryResponse> events = eventService.getUpcomingEventsByLocation(location, pageable);
//        return Response.success("Upcoming events by location retrieved successfully", events);
//    }
//
//    @GetMapping("/sorted")
//    public ResponseEntity<Response<Page<EventSummaryResponse>>> getUpcomingEventsSortedByClosestTime(
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer limit,
//            @RequestParam(required = false) String sortDirection,
//            @RequestParam(required = false) String sortBy) {
//        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
//        Page<EventSummaryResponse> events = eventService.getUpcomingEventsSortedByClosestTime(pageable);
//        return Response.success("Upcoming events sorted by closest time retrieved successfully", events);
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<Response<Page<EventSummaryResponse>>> searchUpcomingEvents(
//            @RequestParam String keyword,
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer limit,
//            @RequestParam(required = false) String sortDirection,
//            @RequestParam(required = false) String sortBy) {
//        CustomPageable pageable = PaginationUtil.createPageable(page, limit, sortDirection, sortBy);
//        Page<EventSummaryResponse> events = eventService.searchUpcomingEvents(keyword, pageable);
//        return Response.success("Upcoming events search results retrieved successfully", events);
//    }
    @GetMapping
    public ResponseEntity<Response<Page<EventSummaryResponse>>> getFilteredEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Instant fromDate,
            @RequestParam(required = false) Instant untilDate,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "id") String sortBy) {
        EventCategory eventCategory = null;
        if (category != null) {
            try {
                eventCategory = EventCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid event category: " + category);
            }
        }
        GetAllEventsParams params = new GetAllEventsParams();
        params.setCategory(category != null ? EventCategory.valueOf(category.toUpperCase()) : null);
        params.setFilter(filter);
        params.setMinPrice(minPrice);
        params.setMaxPrice(maxPrice);
        params.setFromDate(fromDate);
        params.setUntilDate(untilDate);
        params.setLocation(location);
        params.setUserId(userId);
        params.setIsFree(isFree);
        params.setCustomPageable(PaginationUtil.createPageable(page, limit, sortDirection, sortBy));

        Page<EventSummaryResponse> events = eventService.getFilteredEvents(params);
        return Response.success("Filtered events fetched successfully", events);
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
