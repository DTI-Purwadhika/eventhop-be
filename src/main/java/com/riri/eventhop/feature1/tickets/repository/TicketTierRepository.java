package com.riri.eventhop.feature1.tickets.repository;

import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.tickets.entity.TicketTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketTierRepository extends JpaRepository<TicketTier, Long> {
    Optional<TicketTier> findByEventAndName(Event event, String ticketType);
}
