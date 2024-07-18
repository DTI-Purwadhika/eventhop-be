package com.riri.eventhop.feature1.tickets.repository;

import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.tickets.entity.Ticket;
import com.riri.eventhop.feature2.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user);
    List<Ticket> findByEvent(Event event);
    List<Ticket> findByEventOrganizer(User organizer);

    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t WHERE t.event.id = :eventId AND t.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumRevenueByEventIdAndDateRange(Long eventId, Instant startDate, Instant endDate);

    @Query("SELECT COUNT(DISTINCT t.user.id) FROM Ticket t WHERE t.event.id = :eventId AND t.createdAt BETWEEN :startDate AND :endDate")
    Integer countAttendeesByEventIdAndDateRange(Long eventId, Instant startDate, Instant endDate);
}

