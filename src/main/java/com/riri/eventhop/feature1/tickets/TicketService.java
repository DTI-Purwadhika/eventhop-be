package com.riri.eventhop.feature1.tickets;

import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.tickets.entity.Ticket;
import com.riri.eventhop.feature2.users.entity.User;

import java.util.List;

public interface TicketService {
    Ticket purchaseTicket(Long eventId, String tierName, User user, String promotionCode, Integer pointsToUse, boolean useReferralDiscount);
    List<Ticket> getPurchaseHistory(User user);
    List<Ticket> getTransactionHistoryForOrganizer(User organizer);
}