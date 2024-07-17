package com.riri.eventhop.feature1.events.service.impl;

import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.feature1.events.dto.*;
import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.repository.EventRepository;
import com.riri.eventhop.feature1.events.service.EventService;
import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.feature1.tickets.entity.TicketTier;
import com.riri.eventhop.feature1.tickets.dto.TicketTierRequest;
import com.riri.eventhop.feature1.tickets.dto.TicketTierResponse;
import com.riri.eventhop.feature2.users.auth.AuthService;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.feature2.users.entity.UserRole;
import com.riri.eventhop.feature2.users.UserRepository;
import com.riri.eventhop.util.CustomPageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
//    private final ImageStorageService imageStorageService;
//    @Cacheable(value = "upcomingEvents", key = "#pageable", unless = "#result.isEmpty()")
//    @Override
//    public Page<EventSummaryResponse> getAllUpcomingEvents(CustomPageable pageable) {
//        if (pageable == null) {
//            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Pageable cannot be null");
//        }
//        Instant now = Instant.now();
//        Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndStartTimeAfter(now, pageable.toPageRequest());
//        return mapEventPageToSummaryResponsePage(eventsPage);
//    }
//
//    @Cacheable(value = "upcomingEventsByCategory", key = "#category + ':' + #pageable", unless = "#result.isEmpty()")
//    @Override
//    public Page<EventSummaryResponse> getUpcomingEventsByCategory(String category, CustomPageable pageable) {
//        if (category == null || pageable == null) {
//            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Category and pageable cannot be null");
//        }
//        try {
//            Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndEventCategoryAndStartTimeAfter(
//                    EventCategory.valueOf(category.toUpperCase()), Instant.now(), pageable.toPageRequest());
//            return mapEventPageToSummaryResponsePage(eventsPage);
//        } catch (IllegalArgumentException e) {
//            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid event category: " + category);
//        }
//    }
//
//    @Cacheable(value = "upcomingEventsByLocation", key = "#location + ':' + #pageable", unless = "#result.isEmpty()")
//    @Override
//    public Page<EventSummaryResponse> getUpcomingEventsByLocation(String location, CustomPageable pageable) {
//        if (location == null || pageable == null) {
//            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Location and pageable cannot be null");
//        }
//        Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndLocationContainingIgnoreCaseAndStartTimeAfter(
//                location, Instant.now(), pageable.toPageRequest());
//        return mapEventPageToSummaryResponsePage(eventsPage);
//    }
//
//    @Cacheable(value = "upcomingEventsSorted", key = "#pageable", unless = "#result.isEmpty()")
//    @Override
//    public Page<EventSummaryResponse> getUpcomingEventsSortedByClosestTime(CustomPageable pageable) {
//        if (pageable == null) {
//            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Pageable cannot be null");
//        }
//        Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndStartTimeAfterOrderByStartTimeAsc(Instant.now(), pageable.toPageRequest());
//        return mapEventPageToSummaryResponsePage(eventsPage);
//    }
//
//    @Cacheable(value = "upcomingEventsSearch", key = "#keyword + ':' + #pageable", unless = "#result.isEmpty()")
//    @Override
//    public Page<EventSummaryResponse> searchUpcomingEvents(String keyword, CustomPageable pageable) {
//        if (keyword == null || pageable == null) {
//            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Keyword and pageable cannot be null");
//        }
//        Instant now = Instant.now();
//        Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndStartTimeAfterAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndStartTimeAfterAndDescriptionContainingIgnoreCase(
//                now, keyword, now, keyword, pageable.toPageRequest());
//        return mapEventPageToSummaryResponsePage(eventsPage);
//    }

    @Override
    @Cacheable(value = "filteredEvents", key = "#params.toString()", unless = "#result.isEmpty()", cacheManager = "cacheManager")
    public Page<EventSummaryResponse> getFilteredEvents(GetAllEventsParams params) {
        CustomPageable customPageable = params.getCustomPageable();

        String category = params.getCategory() != null ? params.getCategory().name() : null;
        String minPrice = params.getMinPrice() != null ? params.getMinPrice().toString() : null;
        String maxPrice = params.getMaxPrice() != null ? params.getMaxPrice().toString() : null;
        String fromDate = params.getFromDate() != null ? params.getFromDate().toString() : null;
        String untilDate = params.getUntilDate() != null ? params.getUntilDate().toString() : null;
        String userId = params.getUserId() != null ? params.getUserId().toString() : null;
        Boolean isFree = params.getIsFree();
        int offset = (int) customPageable.toPageRequest().getOffset();
        int limit = customPageable.toPageRequest().getPageSize();

        List<Event> events = eventRepository.findFilteredEvents(
                category,
                params.getFilter(),
                minPrice,
                maxPrice,
                fromDate,
                untilDate,
                params.getLocation(),
                userId,
                isFree,
                offset,
                limit
        );

        long totalCount = countFilteredEvents(params);

        return new PageImpl<>(events.stream()
                .map(this::mapEventToSummaryResponse)
                .collect(Collectors.toList()),
                params.getCustomPageable().toPageRequest(),
                totalCount);
    }

    private long countFilteredEvents(GetAllEventsParams params) {
        String category = params.getCategory() != null ? params.getCategory().name() : null;
        String minPrice = params.getMinPrice() != null ? params.getMinPrice().toString() : null;
        String maxPrice = params.getMaxPrice() != null ? params.getMaxPrice().toString() : null;
        String fromDate = params.getFromDate() != null ? params.getFromDate().toString() : null;
        String untilDate = params.getUntilDate() != null ? params.getUntilDate().toString() : null;
        String userId = params.getUserId() != null ? params.getUserId().toString() : null;
        Boolean isFree = params.getIsFree();
        return eventRepository.countFilteredEvents(
                category,
                params.getFilter(),
                minPrice,
                maxPrice,
                fromDate,
                untilDate,
                params.getLocation(),
                userId,
                isFree
        );
    }

    @CacheEvict(value = "filteredEvents", allEntries = true)
    public void evictFilteredEventsCache() {
        // This method is intentionally empty.
        // The @CacheEvict annotation takes care of evicting the cache.
    }
    @Override
    @PreAuthorize("hasRole('ORGANIZER')")
    @Cacheable(value = "eventsByOrganizer", key = "#organizerId + ':' + #pageable", unless = "#result.isEmpty()")
    public Page<EventSummaryResponse> getEventsByOrganizer(Long organizerId, CustomPageable pageable) {
        if (organizerId == null || pageable == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Organizer ID and pageable cannot be null");
        }

        // Check if the organizer exists
        if (!userRepository.existsById(organizerId)) {
            throw new ResourceNotFoundException("Organizer not found with id " + organizerId);
        }

        Page<Event> eventsPage = eventRepository.findByOrganizerIdAndDeletedAtIsNull(organizerId, pageable.toPageRequest());
        return mapEventPageToSummaryResponsePage(eventsPage);
    }
    @Override
    public Event getEventEntityById(Long id) {
        return eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
    }

    @Override
    public EventDetailsResponse getEventById(Long id) {
        if (id == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Event id cannot be null");
        }
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Event not found with id " + id));
        return mapEventToDetailsResponse(event);
    }

    @CacheEvict(value = "filteredEvents", allEntries = true)
    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public EventDetailsResponse createEvent(EventDetailsRequest eventDetailsRequest) {
        String userEmail = authService.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with email: " + userEmail));

        log.info("Attempting to create event for user with email: {}", userEmail);

        Event event = new Event();
        mapEventDetailsRequestToEvent(eventDetailsRequest, event);
        event.setOrganizer(user);
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());

        for (TicketTierRequest tierRequest : eventDetailsRequest.getTicketTiers()) {
            TicketTier ticketTier = new TicketTier();
            ticketTier.setName(tierRequest.getName());
            ticketTier.setPrice(tierRequest.getPrice());
            ticketTier.setQuota(tierRequest.getQuota());
            event.addTicketTier(ticketTier);
        }

        Event savedEvent = eventRepository.save(event);

        if (!user.getRoles().contains(UserRole.ORGANIZER)) {
            user.getRoles().add(UserRole.ORGANIZER);
            userRepository.save(user);
        }

        return mapEventToDetailsResponse(savedEvent);
    }

    @CacheEvict(value = "filteredEvents", allEntries = true)
    @Override
    @Transactional
    @PreAuthorize("hasRole('ORGANIZER')")
    public EventDetailsResponse updateEvent(Long id, EventDetailsRequest eventDetailsRequest) {
        String currentUserEmail = authService.getCurrentUserEmail();
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
        if (existingEvent.isDeleted()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Cannot update a deleted event");
        }

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!existingEvent.getOrganizer().getEmail().equals(currentUserEmail) && !currentUser.getRoles().contains(UserRole.ORGANIZER)) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "You are not authorized to update this event");
        }

        // Check if the image URL has changed
//        String oldImageUrl = existingEvent.getImageUrl();
//        String newImageUrl = eventDetailsRequest.getImageUrl();
//        if (newImageUrl != null && !newImageUrl.equals(oldImageUrl)) {
//            if (oldImageUrl != null) {
//                // Delete the old image
//                imageStorageService.deleteImage(oldImageUrl);
//            }
//            // Update the image URL
//            existingEvent.setImageUrl(newImageUrl);
//        }

        mapEventDetailsRequestToEvent(eventDetailsRequest, existingEvent);
        existingEvent.setUpdatedAt(Instant.now());

        existingEvent.getTicketTiers().clear();
        for (TicketTierRequest tierRequest : eventDetailsRequest.getTicketTiers()) {
            TicketTier ticketTier = new TicketTier();
            ticketTier.setName(tierRequest.getName());
            ticketTier.setPrice(tierRequest.getPrice());
            ticketTier.setQuota(tierRequest.getQuota());
            existingEvent.addTicketTier(ticketTier);
        }

        Event savedEvent = eventRepository.save(existingEvent);
        return mapEventToDetailsResponse(savedEvent);
    }

    @CacheEvict(value = "filteredEvents", allEntries = true)
    @Override
    @Transactional
    @PreAuthorize("hasRole('ORGANIZER')")
    public void deleteEvent(Long id) {
        String currentUserEmail = authService.getCurrentUserEmail();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!event.getOrganizer().getEmail().equals(currentUserEmail) && !currentUser.getRoles().contains(UserRole.ORGANIZER)) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "You are not authorized to delete this event");
        }
        // Delete the associated image if it exists
//        String imageUrl = event.getImageUrl();
//        if (imageUrl != null && !imageUrl.isEmpty()) {
//            imageStorageService.deleteImage(imageUrl);
//        }
        event.softDelete();
        event.setUpdatedAt(Instant.now());
        eventRepository.save(event);
        evictFilteredEventsCache();
    }

//    private User mapUserToOrganizer(User user) {
//        Organizer organizer = new Organizer();
//        organizer.setId(user.getId());
//        organizer.setName(user.getName());
//        // Set other fields as needed
//        return organizer;
//    }
    private EventDetailsResponse mapEventToDetailsResponse(Event event) {
        EventDetailsResponse response = new EventDetailsResponse();
        response.setId(event.getId());  // Include the id
        response.setCode(event.getCode());
        response.setTitle(event.getTitle());
        response.setEventCategory(event.getEventCategory());
        response.setDescription(event.getDescription());
        response.setImageUrl(event.getImageUrl());
        response.setAddress(event.getAddress());
        response.setLocation(event.getLocation());
        response.setStartTime(event.getStartTime());
        response.setEndTime(event.getEndTime());
        response.setPrice(event.getPrice());
        response.setIsFree(event.getIsFree());
        response.setAvailableSeats(event.getAvailableSeats());
        response.setEventUrl(event.getEventUrl());
        response.setOrganizer(event.getOrganizer().getName());
        response.setTicketTiers(event.getTicketTiers().stream()
                .map(this::mapTicketTierToResponse)
                .collect(Collectors.toList()));
        return response;
    }

    private EventSummaryResponse mapEventToSummaryResponse(Event event) {
        EventSummaryResponse response = new EventSummaryResponse();
        response.setId(event.getId());  // Include the id
        response.setTitle(event.getTitle());
        response.setEventCategory(event.getEventCategory());
        response.setImageUrl(event.getImageUrl());
        response.setStartTime(event.getStartTime());
        response.setLocation(event.getLocation());
        response.setPrice(event.getPrice());
        response.setIsFree(event.getIsFree());
        response.setOrganizer(event.getOrganizer().getName());
        return response;
    }

    private Page<EventSummaryResponse> mapEventPageToSummaryResponsePage(Page<Event> eventsPage) {
        return new PageImpl<>(
                eventsPage.getContent().stream()
                        .map(this::mapEventToSummaryResponse)
                        .collect(Collectors.toList()),
                eventsPage.getPageable(),
                eventsPage.getTotalElements()
        );
    }
    private TicketTierResponse mapTicketTierToResponse(TicketTier ticketTier) {
        TicketTierResponse response = new TicketTierResponse();
        response.setId(ticketTier.getId());
        response.setName(ticketTier.getName());
        response.setPrice(ticketTier.getPrice());
        response.setQuota(ticketTier.getQuota());
        response.setRemainingQuota(ticketTier.getRemainingQuota());
        return response;
    }

    private void mapEventDetailsRequestToEvent(EventDetailsRequest eventDetailsRequest, Event event) {
        event.setTitle(eventDetailsRequest.getTitle());
        event.setCode(eventDetailsRequest.getCode());
        event.setEventCategory(eventDetailsRequest.getEventCategory());
        event.setDescription(eventDetailsRequest.getDescription());
        event.setImageUrl(eventDetailsRequest.getImageUrl());
        event.setAddress(eventDetailsRequest.getAddress());
        event.setLocation(eventDetailsRequest.getLocation());
        event.setStartTime(eventDetailsRequest.getStartTime());
        event.setEndTime(eventDetailsRequest.getEndTime());
        event.setPrice(eventDetailsRequest.getPrice());
        event.setIsFree(eventDetailsRequest.getIsFree());
        event.setAvailableSeats(eventDetailsRequest.getAvailableSeats());
        event.setEventUrl(eventDetailsRequest.getEventUrl());
    }

}
