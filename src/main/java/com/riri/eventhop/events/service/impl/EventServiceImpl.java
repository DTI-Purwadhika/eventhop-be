package com.riri.eventhop.events.service.impl;

import com.riri.eventhop.events.service.EventService;
import com.riri.eventhop.events.dto.CreateEventRequest;
import com.riri.eventhop.events.dto.EventDTO;
import com.riri.eventhop.events.dto.EventSummaryDTO;
import com.riri.eventhop.events.dto.UpdateEventRequest;
import com.riri.eventhop.events.entity.Event;
import com.riri.eventhop.events.enums.Category;
import com.riri.eventhop.events.repository.EventRepository;
import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.users.entity.User;
import com.riri.eventhop.users.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Autowired  // Optionally add @Autowired here to be explicit
    public EventServiceImpl(EventRepository eventRepository, ModelMapper modelMapper, UserRepository userRepository){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }
    public List<Event> getEvents(String filter, String category, int limit, int page) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        // Implement your filtering logic here
        Specification<Event> spec = (root, query, criteriaBuilder) -> {
            // Add your filtering criteria here
            return criteriaBuilder.conjunction();
        };
        return eventRepository.findAll(spec, pageable).getContent();
    }
    @Override
    @Transactional
    public EventDTO createEvent(CreateEventRequest request, String clerkId, List<String> imageUrls) {
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = modelMapper.map(request, Event.class);
        event.setOrganizer(user);
        event.setCode(UUID.randomUUID().toString());
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());
        event.setImageUrls(new HashSet<>(imageUrls));

        if (user.getOrganizerName() == null) {
            // Use a default name or prompt the user to set an organizer name
            user.setOrganizerName("New Organizer");
        }

        Event savedEvent = eventRepository.save(event);
        return modelMapper.map(savedEvent, EventDTO.class);
    }

    @Override
    public Page<EventSummaryDTO> getEventsByOrganizer(Long userId, Pageable pageable) {
        return eventRepository.findByOrganizerId(userId, pageable)
                .map(event -> modelMapper.map(event, EventSummaryDTO.class));
    }

//    @Override
//    public DashboardDTO getOrganizerDashboard(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        DashboardDTO dashboard = new DashboardDTO();
//        dashboard.setOrganizerName(user.getOrganizerName());
//        dashboard.setTotalEvents(eventRepository.countByOrganizerId(userId));
//        dashboard.setUpcomingEvents(eventRepository.countByOrganizerIdAndStartTimeAfter(userId, Instant.now()));
//        dashboard.setPastEvents(eventRepository.countByOrganizerIdAndEndTimeBefore(userId, Instant.now()));
//
//        return dashboard;
//    }

    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return modelMapper.map(event, EventDTO.class);
    }

    @Override
    public Page<EventSummaryDTO> getAllEvents(Pageable pageable) {
        return eventRepository.findAllActive(pageable)
                .map(this::convertToSummaryDTO);
    }

    @Override
    public Page<EventSummaryDTO> getEventsByCategory(Category category, Pageable pageable) {
        return eventRepository.findByCategory(category, pageable)
                .map(this::convertToSummaryDTO);
    }

    @Override
    public Page<EventSummaryDTO> getEventsByLocation(String location, Pageable pageable) {
        return eventRepository.findByLocation(location, pageable)
                .map(this::convertToSummaryDTO);
    }

    private EventSummaryDTO convertToSummaryDTO(Event event) {
        return modelMapper.map(event, EventSummaryDTO.class);
    }

    @Override
    public Page<EventSummaryDTO> searchEvents(String keyword, Pageable pageable) {
        return eventRepository.searchEvents(keyword, pageable)
                .map(this::convertToSummaryDTO);
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long id, UpdateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Update only the fields that are provided in the request
        if (request.getName() != null) event.setName(request.getName());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getAddress() != null) event.setAddress(request.getAddress());
        if (request.getStartTime() != null) event.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) event.setEndTime(request.getEndTime());
        if (request.getPrice() != null) event.setPrice(request.getPrice());
        if (request.getAvailableSeats() != null) event.setAvailableSeats(request.getAvailableSeats());
        if (request.getMainImage() != null) event.setMainImage(request.getMainImage());
        if (request.getCategory() != null) event.setCategory(request.getCategory());
        if (request.getLocation() != null) event.setLocation(request.getLocation());

        event.setUpdatedAt(Instant.now());

        Event updatedEvent = eventRepository.save(event);
        return modelMapper.map(updatedEvent, EventDTO.class);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.setDeletedAt(Instant.now());
        eventRepository.save(event);
    }
}