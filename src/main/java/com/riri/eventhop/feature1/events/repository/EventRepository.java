package com.riri.eventhop.feature1.events.repository;

import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.entity.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
//    Page<Event> findByDeletedAtIsNullAndStartTimeAfter(Instant now, Pageable pageable);
//    Page<Event> findByDeletedAtIsNullAndEventCategoryAndStartTimeAfter(EventCategory eventCategory, Instant now, Pageable pageable);
//    Page<Event> findByDeletedAtIsNullAndLocationContainingIgnoreCaseAndStartTimeAfter(String location, Instant now, Pageable pageable);
//    Page<Event> findByDeletedAtIsNullAndStartTimeAfterOrderByStartTimeAsc(Instant now, Pageable pageable);
//    Page<Event> findByDeletedAtIsNullAndStartTimeAfterAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndStartTimeAfterAndDescriptionContainingIgnoreCase(Instant now, String title, Instant now2, String description, Pageable pageable);
@Query(value = "SELECT * FROM events e WHERE " +
        "e.deleted_at IS NULL " +  // Add this line to exclude deleted events
        "AND (:category IS NULL OR e.event_category = CAST(:category AS VARCHAR)) " +
        "AND (:filter IS NULL OR e.title LIKE CONCAT('%', :filter, '%')) " +
        "AND (:minPrice IS NULL OR e.price >= CAST(:minPrice AS DECIMAL)) " +
        "AND (:maxPrice IS NULL OR e.price <= CAST(:maxPrice AS DECIMAL)) " +
        "AND (:fromDate IS NULL OR e.start_time >= CAST(:fromDate AS TIMESTAMP)) " +
        "AND (:untilDate IS NULL OR e.end_time <= CAST(:untilDate AS TIMESTAMP)) " +
        "AND (:location IS NULL OR e.locations = :location) " +
        "AND (:userId IS NULL OR e.organizer_id = CAST(:userId AS BIGINT)) " +
        "AND (:isFree IS NULL OR e.is_free = :isFree) " +
        "ORDER BY e.id " +
        "OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY",
        nativeQuery = true)
List<Event> findFilteredEvents(
        @Param("category") String category,
        @Param("filter") String filter,
        @Param("minPrice") String minPrice,
        @Param("maxPrice") String maxPrice,
        @Param("fromDate") String fromDate,
        @Param("untilDate") String untilDate,
        @Param("location") String location,
        @Param("userId") String userId,
        @Param("isFree") Boolean isFree,
        @Param("offset") int offset,
        @Param("limit") int limit);
    @Query(value = "SELECT COUNT(*) FROM events e WHERE " +
            "e.deleted_at IS NULL " +  // Add this line to exclude deleted events
            "AND (:category IS NULL OR e.event_category = CAST(:category AS VARCHAR)) " +
            "AND (:filter IS NULL OR e.title LIKE CONCAT('%', :filter, '%')) " +
            "AND (:minPrice IS NULL OR e.price >= CAST(:minPrice AS DECIMAL)) " +
            "AND (:maxPrice IS NULL OR e.price <= CAST(:maxPrice AS DECIMAL)) " +
            "AND (:fromDate IS NULL OR e.start_time >= CAST(:fromDate AS TIMESTAMP)) " +
            "AND (:untilDate IS NULL OR e.end_time <= CAST(:untilDate AS TIMESTAMP)) " +
            "AND (:location IS NULL OR e.locations = :location) " +
            "AND (:userId IS NULL OR e.organizer_id = CAST(:userId AS BIGINT))" +
            "AND (:isFree IS NULL OR e.is_free = :isFree)",
            nativeQuery = true)
    long countFilteredEvents(
            @Param("category") String category,
            @Param("filter") String filter,
            @Param("minPrice") String minPrice,
            @Param("maxPrice") String maxPrice,
            @Param("fromDate") String fromDate,
            @Param("untilDate") String untilDate,
            @Param("location") String location,
            @Param("userId") String userId,
            @Param("isFree") Boolean isFree);
    Optional<Event> findByIdAndDeletedAtIsNull(Long id);
}
