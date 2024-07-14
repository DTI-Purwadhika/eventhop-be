package com.riri.eventhop.feature1.reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEventId(Long eventId);
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    Page<Review> findByEventIdOrderByCreatedAtDesc(Long eventId, Pageable pageable);
    Page<Review> findByEventOrganizerIdOrderByCreatedAtDesc(Long organizerId, Pageable pageable);
}
