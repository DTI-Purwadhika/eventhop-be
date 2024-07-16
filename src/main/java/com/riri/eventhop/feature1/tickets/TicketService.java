package com.riri.eventhop.feature1.tickets;

import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature2.users.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface TicketService {
    Ticket purchaseTicket(Long eventId, String tierName, User user, String promotionCode, Integer pointsToUse, boolean useReferralDiscount);    //    Ticket purchaseTicket(User user, Long eventId, Integer pointsToRedeem, String promotionCode);

}