package com.riri.eventhop.events.controller;

//import com.riri.eventhop.Image.ImageStorageService;
import com.riri.eventhop.DashboardDTO;
import com.riri.eventhop.events.service.EventService;
import com.riri.eventhop.events.dto.CreateEventRequest;
import com.riri.eventhop.events.dto.EventDTO;
import com.riri.eventhop.events.dto.EventSummaryDTO;
import com.riri.eventhop.events.dto.UpdateEventRequest;
import com.riri.eventhop.events.enums.Category;
import com.riri.eventhop.response.Response;
import com.riri.eventhop.users.entity.CustomUserDetails;
import com.riri.eventhop.users.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private EventService eventService;
    private final UserService userService;
    private PagedResourcesAssembler<EventSummaryDTO> pagedResourcesAssembler;
//    private ImageStorageService imageStorageService;
    public EventController(EventService eventService, PagedResourcesAssembler<EventSummaryDTO> pagedResourcesAssembler, UserService userService
//            , ImageStorageService imageStorageService
    ){
        this.eventService = eventService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.userService = userService;
//        this.imageStorageService = imageStorageService;
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Response<EventDTO>> createEvent(@RequestBody CreateEventRequest request,
                                                          @RequestParam("imageUrls") List<String> imageUrls,
                                                          Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        EventDTO createdEvent = eventService.createEvent(request, userDetails.getUser().getId(), imageUrls);
        return Response.success("Event created successfully", createdEvent);
    }

    @GetMapping("/organizer")
    public ResponseEntity<Response<PagedModel<EntityModel<EventSummaryDTO>>>> getOrganizerEvents(
            Authentication authentication, Pageable pageable) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Page<EventSummaryDTO> events = eventService.getEventsByOrganizer(userDetails.getUser().getId(), pageable);
        PagedModel<EntityModel<EventSummaryDTO>> pagedModel = pagedResourcesAssembler.toModel(events);
        return Response.success("Organizer events retrieved successfully", pagedModel);
    }

    @GetMapping("/organizer/dashboard")
    public ResponseEntity<Response<DashboardDTO>> getOrganizerDashboard(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        DashboardDTO dashboard = eventService.getOrganizerDashboard(userDetails.getUser().getId());
        return Response.success("Organizer dashboard retrieved successfully", dashboard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<EventDTO>> getEvent(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return Response.success("Event retrieved successfully", event);
    }

    @GetMapping
    public ResponseEntity<Response<PagedModel<EntityModel<EventSummaryDTO>>>> getAllEvents(Pageable pageable) {
        Page<EventSummaryDTO> events = eventService.getAllEvents(pageable);
        PagedModel<EntityModel<EventSummaryDTO>> pagedModel = pagedResourcesAssembler.toModel(events);
        return Response.success("Events retrieved successfully", pagedModel);
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedModel<EntityModel<EventSummaryDTO>>>> searchEvents(@RequestParam String keyword, Pageable pageable) {
        Page<EventSummaryDTO> events = eventService.searchEvents(keyword, pageable);
        PagedModel<EntityModel<EventSummaryDTO>> pagedModel = pagedResourcesAssembler.toModel(events);
        return Response.success("Events searched successfully", pagedModel);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Response<PagedModel<EntityModel<EventSummaryDTO>>>> getEventsByCategory(@PathVariable Category category, Pageable pageable) {
        Page<EventSummaryDTO> events = eventService.getEventsByCategory(category, pageable);
        PagedModel<EntityModel<EventSummaryDTO>> pagedModel = pagedResourcesAssembler.toModel(events);
        return Response.success("Events retrieved successfully", pagedModel);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<Response<PagedModel<EntityModel<EventSummaryDTO>>>> getEventsByLocation(@PathVariable String location, Pageable pageable) {
        Page<EventSummaryDTO> events = eventService.getEventsByLocation(location, pageable);
        PagedModel<EntityModel<EventSummaryDTO>> pagedModel = pagedResourcesAssembler.toModel(events);
        return Response.success("Events retrieved successfully", pagedModel);
    }
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<Response<EventDTO>> updateEvent(@PathVariable Long id, @RequestBody UpdateEventRequest request) {
        EventDTO updatedEvent = eventService.updateEvent(id, request);
        return Response.success("Event updated successfully", updatedEvent);
    }
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return Response.success("Event deleted successfully");
    }
}
