package com.riri.eventhop.feature1.reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByEventId(Long eventId, Pageable pageable);
    List<Review> findByEventId(Long eventId);
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    Page<Review> findByEventOrganizerIdOrderByCreatedAtDesc(Long organizerId, PageRequest pageRequest);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.event.id = :eventId AND r.createdAt BETWEEN :startDate AND :endDate")
    Double averageRatingByEventIdAndDateRange(Long eventId, Instant startDate, Instant endDate);
}
