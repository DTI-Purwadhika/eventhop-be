package com.riri.eventhop.feature1.tickets.dto;

import lombok.Data;

@Data
public class TicketPurchaseRequest {
    private Long eventId;
    private String tierName;
    private String promotionCode;
    private Integer pointsToUse;
    private boolean useReferralDiscount;
}