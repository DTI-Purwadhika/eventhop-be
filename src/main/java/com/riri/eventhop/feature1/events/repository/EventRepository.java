package com.riri.eventhop.feature1.events.repository;

import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.entity.EventCategory;
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
    Page<Event> findByDeletedAtIsNullAndLocationContainingIgnoreCaseAndStartTimeAfter(String location, Instant now, Pageable pageable);
    Page<Event> findByDeletedAtIsNullAndStartTimeAfterOrderByStartTimeAsc(Instant now, Pageable pageable);
    Page<Event> findByDeletedAtIsNullAndStartTimeAfterAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndStartTimeAfterAndDescriptionContainingIgnoreCase(Instant now, String title, Instant now2, String description, Pageable pageable);
    Optional<Event> findByIdAndDeletedAtIsNull(Long id);
}
