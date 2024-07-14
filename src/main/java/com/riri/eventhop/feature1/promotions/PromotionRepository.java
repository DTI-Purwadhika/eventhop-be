package com.riri.eventhop.feature1.promotions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Page<Promotion> findByEventId(Long eventId, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.event.id = :eventId AND p.startDate <= :now AND p.expireDate > :now")
    Page<Promotion> findActivePromotionsForEvent(Long eventId, LocalDateTime now, Pageable pageable);

    Optional<Promotion> findByCode(String promotionCode);
    @Query("SELECT p FROM Promotion p JOIN p.event e WHERE e.organizer.id = :organizerId")
    Page<Promotion> findAllByOrganizerId(@Param("organizerId") Long organizerId, Pageable pageable);
}