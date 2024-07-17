package com.riri.eventhop.feature1.events.repository;

import com.riri.eventhop.feature1.events.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT DISTINCT e.* FROM events e " +
            "JOIN ticket_tiers tt ON e.id = tt.event_id " +
            "WHERE e.deleted_at IS NULL " +
            "AND (:category IS NULL OR e.event_category = :category) " +
            "AND (:filter IS NULL OR (LOWER(e.title) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :filter, '%')))) " +
            "AND (:minTicketPrice IS NULL OR tt.price >= CAST(:minTicketPrice AS DECIMAL)) " +
            "AND (:maxTicketPrice IS NULL OR tt.price <= CAST(:maxTicketPrice AS DECIMAL)) " +
            "AND (:fromDate IS NULL OR e.start_time >= CAST(:fromDate AS TIMESTAMP)) " +
            "AND (:untilDate IS NULL OR e.end_time <= CAST(:untilDate AS TIMESTAMP)) " +
            "AND (:location IS NULL OR e.locations = :location) " +
            "AND (:userId IS NULL OR e.organizer_id = CAST(:userId AS BIGINT)) " +
            "AND (:isFree IS NULL OR e.is_free = :isFree) " +
            "ORDER BY e.id " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Event> findFilteredEvents(
            @Param("category") String category,
            @Param("filter") String filter,
            @Param("minTicketPrice") String minTicketPrice,
            @Param("maxTicketPrice") String maxTicketPrice,
            @Param("fromDate") String fromDate,
            @Param("untilDate") String untilDate,
            @Param("location") String location,
            @Param("userId") String userId,
            @Param("isFree") Boolean isFree,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Query(value = "SELECT COUNT(DISTINCT e.id) FROM events e " +
            "JOIN ticket_tiers tt ON e.id = tt.event_id " +
            "WHERE e.deleted_at IS NULL " +
            "AND (:category IS NULL OR e.event_category = :category) " +
            "AND (:filter IS NULL OR (LOWER(e.title) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :filter, '%')))) " +
            "AND (:minTicketPrice IS NULL OR tt.price >= CAST(:minTicketPrice AS DECIMAL)) " +
            "AND (:maxTicketPrice IS NULL OR tt.price <= CAST(:maxTicketPrice AS DECIMAL)) " +
            "AND (:fromDate IS NULL OR e.start_time >= CAST(:fromDate AS TIMESTAMP)) " +
            "AND (:untilDate IS NULL OR e.end_time <= CAST(:untilDate AS TIMESTAMP)) " +
            "AND (:location IS NULL OR e.locations = :location) " +
            "AND (:userId IS NULL OR e.organizer_id = CAST(:userId AS BIGINT)) " +
            "AND (:isFree IS NULL OR e.is_free = :isFree)",
            nativeQuery = true)
    long countFilteredEvents(
            @Param("category") String category,
            @Param("filter") String filter,
            @Param("minTicketPrice") String minTicketPrice,
            @Param("maxTicketPrice") String maxTicketPrice,
            @Param("fromDate") String fromDate,
            @Param("untilDate") String untilDate,
            @Param("location") String location,
            @Param("userId") String userId,
            @Param("isFree") Boolean isFree);

    Page<Event> findByOrganizerIdAndDeletedAtIsNull(Long organizerId, Pageable pageable);

    Optional<Event> findByIdAndDeletedAtIsNull(Long id);

//    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer.id = :organizerId AND e.startTime BETWEEN :startDate AND :endDate")
//    Long countByOrganizerAndDateRange(@Param("organizerId") Long organizerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
