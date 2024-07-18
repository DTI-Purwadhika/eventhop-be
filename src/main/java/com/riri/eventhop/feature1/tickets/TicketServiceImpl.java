package com.riri.eventhop.feature1.tickets;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.service.EventService;
import com.riri.eventhop.feature1.promotions.PromotionService;
import com.riri.eventhop.feature1.promotions.dto.PromotionResponse;
import com.riri.eventhop.feature1.tickets.entity.Ticket;
import com.riri.eventhop.feature1.tickets.entity.TicketTier;
import com.riri.eventhop.feature1.tickets.repository.TicketRepository;
import com.riri.eventhop.feature1.tickets.repository.TicketTierRepository;
import com.riri.eventhop.feature2.users.entity.Discount;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.feature2.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final UserService userService;
    private final EventService eventService;
    private final TicketRepository ticketRepository;
    private final PromotionService promotionService;
    private final TicketTierRepository ticketTierRepository;

    @Override
    @Transactional
    public Ticket purchaseTicket(Long eventId, String tierName, User user, String promotionCode, Integer pointsToUse, boolean useReferralDiscount) {
        Event event = eventService.getEventEntityById(eventId);
        TicketTier ticketTier = ticketTierRepository.findByEventAndName(event, tierName)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket tier not found for event and ticket type"));

        if (ticketTier.getRemainingQuota() <= 0) {
            throw new ApplicationException("No more tickets available for this tier");
        }

        BigDecimal price = ticketTier.getPrice();

        // Apply promotion if provided
        if (promotionCode != null && !promotionCode.isEmpty()) {
            PromotionResponse promotion = promotionService.getPromotionByCode(promotionCode);
            if (!promotionService.isPromotionActive(promotion.getId())) {
                throw new ApplicationException("Promotion is not active");
            }
            price = applyPromotion(price, promotion);
            promotionService.incrementUsedCount(promotion.getId());
        }

        // Apply referral discount if requested and available
        if (useReferralDiscount) {
            Discount referralDiscount = userService.getAvailableReferralDiscount(user);
            if (referralDiscount != null) {
                price = applyDiscount(price, referralDiscount);
                userService.useReferralDiscount(user);
            } else {
                throw new ApplicationException("No available referral discount for this user");
            }
        }

        // Apply points if provided
        if (pointsToUse != null && pointsToUse > 0) {
            price = applyPoints(price, pointsToUse, user);
        }

        ticketTier.decreaseQuota(1);
        ticketTierRepository.save(ticketTier);

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setTicketTier(ticketTier);
        ticket.setEvent(event);
        ticket.setPrice(price);
        ticket.setTicketCode(generateTicketCode());
        ticket.setUsed(false);

        return ticketRepository.save(ticket);
    }

    private BigDecimal applyPromotion(BigDecimal price, PromotionResponse promotion) {
        switch (promotion.getType()) {
            case PERCENTAGE:
                BigDecimal discountAmount = price.multiply(promotion.getAmount().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                return price.subtract(discountAmount);
            case FLAT:
            case CASHBACK:
                return price.subtract(promotion.getAmount());
            default:
                throw new ApplicationException("Invalid promotion type");
        }
    }

    private BigDecimal applyDiscount(BigDecimal price, Discount discount) {
        BigDecimal discountAmount = price.multiply(BigDecimal.valueOf(discount.getDiscountPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        return price.subtract(discountAmount);
    }

    private BigDecimal applyPoints(BigDecimal price, int pointsToUse, User user) {
        int availablePoints = userService.calculateAvailablePoints(user);
        if (pointsToUse > availablePoints) {
            throw new ApplicationException("Not enough points available");
        }

        BigDecimal pointValue = BigDecimal.valueOf(pointsToUse); // 1 point = 1 IDR
        BigDecimal discountedPrice = price.subtract(pointValue);
        if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
            discountedPrice = BigDecimal.ZERO;
        }

        userService.redeemPoints(user, pointsToUse);
        return discountedPrice;
    }

    private String generateTicketCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    @Override
    public List<Ticket> getPurchaseHistory(User user) {
        return ticketRepository.findByUser(user);
    }

    @Override
    public List<Ticket> getTransactionHistoryForOrganizer(User organizer) {
        return ticketRepository.findByEventOrganizer(organizer);
    }

}