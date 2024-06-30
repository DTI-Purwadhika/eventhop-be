package com.riri.eventhop.events.service.impl;

import com.riri.eventhop.events.dto.EventDetailsResponse;
import com.riri.eventhop.events.entity.EventCategory;
import com.riri.eventhop.events.entity.Event;
import com.riri.eventhop.events.repository.EventRepository;
import com.riri.eventhop.events.service.EventService;
import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.users.entity.User;
import com.riri.eventhop.users.entity.UserRole;
import com.riri.eventhop.users.repository.UserRepository;
import com.riri.eventhop.util.CustomPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "upcomingEvents", key = "#pageable", unless = "#result.isEmpty()")
    @Override
    public Page<EventDetailsResponse> getAllUpcomingEvents(CustomPageable pageable) {
        if (pageable == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Pageable cannot be null");
        }
        Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndStartTimeAfter(Instant.now(), pageable.toPageRequest());
        return mapEventPageToResponsePage(eventsPage);
    }

    @Cacheable(value = "upcomingEventsByCategory", key = "#category + ':' + #pageable", unless = "#result.isEmpty()")
    @Override
    public Page<EventDetailsResponse> getUpcomingEventsByCategory(String category, CustomPageable pageable) {
        if (category == null || pageable == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Category and pageable cannot be null");
        }
        try {
            Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndEventCategoryAndStartTimeAfter(
                    EventCategory.valueOf(category.toUpperCase()), Instant.now(), pageable.toPageRequest());
            return mapEventPageToResponsePage(eventsPage);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid event category: " + category);
        }
    }

    @Cacheable(value = "upcomingEventsByLocation", key = "#location + ':' + #pageable", unless = "#result.isEmpty()")
    @Override
    public Page<EventDetailsResponse> getUpcomingEventsByLocation(String location, CustomPageable pageable) {
        if (location == null || pageable == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Location and pageable cannot be null");
        }
        Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndLocationContainingAndStartTimeAfter(
                location, Instant.now(), pageable.toPageRequest());
        return mapEventPageToResponsePage(eventsPage);
    }

    @Cacheable(value = "upcomingEventsSorted", key = "#pageable", unless = "#result.isEmpty()")
    @Override
    public Page<EventDetailsResponse> getUpcomingEventsSortedByClosestTime(CustomPageable pageable) {
        if (pageable == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Pageable cannot be null");
        }
        Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndStartTimeAfterOrderByStartTimeAsc(Instant.now(), pageable.toPageRequest());
        return mapEventPageToResponsePage(eventsPage);
    }

    @Cacheable(value = "upcomingEventsSearch", key = "#keyword + ':' + #pageable", unless = "#result.isEmpty()")
    @Override
    public Page<EventDetailsResponse> searchUpcomingEvents(String keyword, CustomPageable pageable) {
        if (keyword == null || pageable == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Keyword and pageable cannot be null");
        }
        Page<Event> eventsPage = eventRepository.findByDeletedAtIsNullAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStartTimeAfter(
                keyword, keyword, Instant.now(), pageable.toPageRequest());
        return mapEventPageToResponsePage(eventsPage);
    }

    @Override
    public EventDetailsResponse getEventById(Long id) {
        if (id == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Event id cannot be null");
        }
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Event not found with id " + id));
        return mapEventToEventResponse(event);
    }

    @CacheEvict(value = {"upcomingEvents", "upcomingEventsByCategory", "upcomingEventsByLocation", "upcomingEventsSorted", "upcomingEventsSearch"}, allEntries = true)
    @Override
    @Transactional
    public EventDetailsResponse createEvent(Event event, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with id " + userId));

        event.setOrganizer(user);
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());
        Event savedEvent = eventRepository.save(event);

        if (!user.getRoles().contains(UserRole.ORGANIZER)) {
            user.getRoles().add(UserRole.ORGANIZER);
            userRepository.save(user);
        }

        return mapEventToEventResponse(savedEvent);
    }

    @CacheEvict(value = {"upcomingEvents", "upcomingEventsByCategory", "upcomingEventsByLocation", "upcomingEventsSorted", "upcomingEventsSearch"}, allEntries = true)
    @Override
    @Transactional
    public EventDetailsResponse updateEvent(Long id, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Event not found with id " + id));
        if (existingEvent.isDeleted()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Cannot update a deleted event");
        }

        // Update fields
        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setCode(updatedEvent.getCode());
        existingEvent.setEventCategory(updatedEvent.getEventCategory());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setMainImage(updatedEvent.getMainImage());
        existingEvent.setImageUrls(updatedEvent.getImageUrls());
        existingEvent.setAddress(updatedEvent.getAddress());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setStartTime(updatedEvent.getStartTime());
        existingEvent.setEndTime(updatedEvent.getEndTime());
        existingEvent.setPrice(updatedEvent.getPrice());
        existingEvent.setAvailableSeats(updatedEvent.getAvailableSeats());
        existingEvent.setUrl(updatedEvent.getUrl());
        existingEvent.setUpdatedAt(Instant.now());

        Event savedEvent = eventRepository.save(existingEvent);
        return mapEventToEventResponse(savedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Event not found with id " + id));
        event.softDelete();
        event.setUpdatedAt(Instant.now());
        eventRepository.save(event);
    }

    @Override
    public EventDetailsResponse mapEventToEventResponse(Event event) {
        EventDetailsResponse response = new EventDetailsResponse();
        response.setId(event.getId());
        response.setCode(event.getCode());
        response.setTitle(event.getTitle());
        response.setEventCategory(event.getEventCategory());
        response.setDescription(event.getDescription());
        response.setMainImage(event.getMainImage());
        response.setImageUrls(event.getImageUrls());
        response.setAddress(event.getAddress());
        response.setLocation(event.getLocation());
        response.setStartTime(event.getStartTime());
        response.setEndTime(event.getEndTime());
        response.setPrice(event.getPrice());
        response.setFree(event.isFree());
        response.setAvailableSeats(event.getAvailableSeats());
        response.setUrl(event.getUrl());
        return response;
    }

    private Page<EventDetailsResponse> mapEventPageToResponsePage(Page<Event> eventsPage) {
        return new PageImpl<>(
                eventsPage.getContent().stream()
                        .map(this::mapEventToEventResponse)
                        .collect(Collectors.toList()),
                eventsPage.getPageable(),
                eventsPage.getTotalElements()
        );
    }
}
