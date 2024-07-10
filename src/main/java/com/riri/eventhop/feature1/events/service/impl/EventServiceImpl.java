package com.riri.eventhop.feature1.events.service.impl;

import com.riri.eventhop.feature1.events.dto.*;
import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.repository.EventRepository;
import com.riri.eventhop.feature1.events.service.EventService;
import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.feature1.images.ImageStorageService;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.feature2.users.entity.UserRole;
import com.riri.eventhop.feature2.users.UserRepository;
import com.riri.eventhop.feature2.users.service.UserService;
import com.riri.eventhop.util.CustomPageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
    private final UserService userService;
    private final ImageStorageService imageStorageService;
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

    @Cacheable(value = "filteredEvents", key = "#params.toString()", unless = "#result.isEmpty()")
    @Override
    public Page<EventSummaryResponse> getFilteredEvents(GetAllEventsParams params) {
        CustomPageable customPageable = params.getCustomPageable();

        String category = params.getCategory() != null ? params.getCategory().name() : null;
        String minPrice = params.getMinPrice() != null ? params.getMinPrice().toString() : null;
        String maxPrice = params.getMaxPrice() != null ? params.getMaxPrice().toString() : null;
        String fromDate = params.getFromDate() != null ? params.getFromDate().toString() : null;
        String untilDate = params.getUntilDate() != null ? params.getUntilDate().toString() : null;
        String userId = params.getUserId() != null ? params.getUserId().toString() : null;

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
                offset,
                limit
        );

        long totalCount = countFilteredEvents(params);

        return new PageImpl<>(events.stream().map(this::mapEventToSummaryResponse).collect(Collectors.toList()),
                customPageable.toPageRequest(), totalCount);
    }

    private long countFilteredEvents(GetAllEventsParams params) {
        String category = params.getCategory() != null ? params.getCategory().name() : null;
        String minPrice = params.getMinPrice() != null ? params.getMinPrice().toString() : null;
        String maxPrice = params.getMaxPrice() != null ? params.getMaxPrice().toString() : null;
        String fromDate = params.getFromDate() != null ? params.getFromDate().toString() : null;
        String untilDate = params.getUntilDate() != null ? params.getUntilDate().toString() : null;
        String userId = params.getUserId() != null ? params.getUserId().toString() : null;

        return eventRepository.countFilteredEvents(
                category,
                params.getFilter(),
                minPrice,
                maxPrice,
                fromDate,
                untilDate,
                params.getLocation(),
                userId
        );
    }

    @CacheEvict(value = "filteredEvents", allEntries = true)
    public void evictFilteredEventsCache() {
        // This method is intentionally empty.
        // The @CacheEvict annotation takes care of evicting the cache.
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
        String userEmail = getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with email: " + userEmail));

        log.info("Attempting to create event for user with email: {}", userEmail);

        Event event = new Event();
        mapEventDetailsRequestToEvent(eventDetailsRequest, event);
        event.setOrganizer(user);
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());

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
        String currentUserEmail = getCurrentUserEmail();
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Event not found with id " + id));
        if (existingEvent.isDeleted()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Cannot update a deleted event");
        }
        if (!existingEvent.getOrganizer().getEmail().equals(currentUserEmail)) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "You are not the organizer of this event");
        }

        // Check if the image URL has changed
        String oldImageUrl = existingEvent.getImageUrl();
        String newImageUrl = eventDetailsRequest.getImageUrl();
        if (newImageUrl != null && !newImageUrl.equals(oldImageUrl)) {
            if (oldImageUrl != null) {
                // Delete the old image
                imageStorageService.deleteImage(oldImageUrl);
            }
            // Update the image URL
            existingEvent.setImageUrl(newImageUrl);
        }

        mapEventDetailsRequestToEvent(eventDetailsRequest, existingEvent);
        existingEvent.setUpdatedAt(Instant.now());

        Event savedEvent = eventRepository.save(existingEvent);
        return mapEventToDetailsResponse(savedEvent);
    }

    @CacheEvict(value = {"upcomingEvents", "upcomingEventsByCategory", "upcomingEventsByLocation", "upcomingEventsSorted", "upcomingEventsSearch"}, allEntries = true)
    @Override
    @Transactional
    @PreAuthorize("hasRole('ORGANIZER')")
    public void deleteEvent(Long id) {
        String currentUserEmail = getCurrentUserEmail();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Event not found with id " + id));
        if (!event.getOrganizer().getEmail().equals(currentUserEmail)) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "You are not the organizer of this event");
        }
        // Delete the associated image if it exists
        String imageUrl = event.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageStorageService.deleteImage(imageUrl);
        }
        event.softDelete();
        event.setUpdatedAt(Instant.now());
        eventRepository.save(event);
        evictFilteredEventsCache();
    }
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }
        throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected authentication type");
    }
    private Organizer mapUserToOrganizer(User user) {
        Organizer organizer = new Organizer();
        organizer.setId(user.getId());
        organizer.setName(user.getName());
        // Set other fields as needed
        return organizer;
    }
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
        response.setFree(event.isFree());
        response.setAvailableSeats(event.getAvailableSeats());
        response.setEventUrl(event.getEventUrl());
        response.setOrganizerName(mapUserToOrganizer(event.getOrganizer()));

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
        response.setFree(event.isFree());
        if (event.getOrganizer() != null) {
            Organizer organizer = new Organizer();
            organizer.setId(event.getOrganizer().getId());
            organizer.setName(event.getOrganizer().getName()); // Adjust based on User entity
            response.setOrganizer(organizer);
        }
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
        event.setAvailableSeats(eventDetailsRequest.getAvailableSeats());
        event.setEventUrl(eventDetailsRequest.getEventUrl());
    }

}
