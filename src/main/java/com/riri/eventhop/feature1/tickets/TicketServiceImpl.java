package com.riri.eventhop.feature1.tickets;

import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.repository.EventRepository;
import com.riri.eventhop.feature1.events.service.EventService;
import com.riri.eventhop.feature1.promotions.PromotionService;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.feature2.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final PromotionService promotionService;
    private final TicketTierRepository ticketTierRepository;

    @Override
    @Transactional
    public Ticket purchaseTicket(Long eventId, String name, User user) {
        Event event = eventService.getEventEntityById(eventId);
        TicketTier ticketTier = ticketTierRepository.findByEventAndName(event, name)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket tier not found for event and ticket type"));

        if (ticketTier.getRemainingQuota() <= 0) {
            throw new IllegalStateException("No more tickets available for this tier");
        }

        ticketTier.decreaseQuota(1);
        ticketTierRepository.save(ticketTier);

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setTicketTier(ticketTier);
        ticket.setEvent(event);
        ticket.setPrice(ticketTier.getPrice());
        ticket.setTicketCode(generateTicketCode());
        ticket.setUsed(false);

        return ticketRepository.save(ticket);
    }
    private String generateTicketCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}