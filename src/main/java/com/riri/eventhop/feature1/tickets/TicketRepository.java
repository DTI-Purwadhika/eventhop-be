package com.riri.eventhop.feature1.tickets;

import com.riri.eventhop.feature1.reviews.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t WHERE t.event.id = :eventId AND t.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumRevenueByEventIdAndDateRange(Long eventId, Instant startDate, Instant endDate);

    @Query("SELECT COUNT(DISTINCT t.user.id) FROM Ticket t WHERE t.event.id = :eventId AND t.createdAt BETWEEN :startDate AND :endDate")
    Integer countAttendeesByEventIdAndDateRange(Long eventId, Instant startDate, Instant endDate);
}

