package com.riri.eventhop.feature1.tickets.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class PurchaseHistory {
    private String title;
    private String ticketTier;
    private Instant purchaseTime;
}
