package com.riri.eventhop.events.repository;

import com.riri.eventhop.events.entity.Event;
import com.riri.eventhop.events.entity.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByDeletedAtIsNullAndStartTimeAfter(Instant now, Pageable pageable);
    Page<Event> findByDeletedAtIsNullAndEventCategoryAndStartTimeAfter(EventCategory eventCategory, Instant now, Pageable pageable);
    Page<Event> findByDeletedAtIsNullAndLocationContainingAndStartTimeAfter(String location, Instant now, Pageable pageable);
    Page<Event> findByDeletedAtIsNullAndStartTimeAfterOrderByStartTimeAsc(Instant now, Pageable pageable);
    Page<Event> findByDeletedAtIsNullAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStartTimeAfter(String title, String description, Instant now, Pageable pageable);
    Optional<Event> findByIdAndDeletedAtIsNull(Long id);
}
