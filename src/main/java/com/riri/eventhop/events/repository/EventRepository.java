package com.riri.eventhop.events.repository;

import com.riri.eventhop.events.entity.Event;
import com.riri.eventhop.events.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL")
    Page<Event> findAllActive(Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.organizer.id = :organizerId AND e.deletedAt IS NULL")
    Page<Event> findByOrganizerId(@Param("organizerId") Long organizerId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.category = :category AND e.deletedAt IS NULL")
    Page<Event> findByCategory(@Param("category") Category category, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.location = :location AND e.deletedAt IS NULL")
    Page<Event> findByLocation(@Param("location") String location, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE (LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND e.deletedAt IS NULL")
    Page<Event> searchEvents(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer.id = :organizerId AND e.deletedAt IS NULL")
    long countByOrganizerId(@Param("organizerId") Long organizerId);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer.id = :organizerId AND e.startTime > :now AND e.deletedAt IS NULL")
    long countByOrganizerIdAndStartTimeAfter(@Param("organizerId") Long organizerId, @Param("now") Instant now);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer.id = :organizerId AND e.endTime < :now AND e.deletedAt IS NULL")
    long countByOrganizerIdAndEndTimeBefore(@Param("organizerId") Long organizerId, @Param("now") Instant now);
}