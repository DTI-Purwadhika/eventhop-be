package com.riri.eventhop.feature1.tickets.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TicketResponse {
    private Long id;
    private String ticketCode;
    private String eventTitle;
    private Long eventId;
    private String ticketTierName;
    private BigDecimal price;
    private Boolean isFree;
    private Instant eventStartTime;
    private Instant eventEndTime;
    private String eventLocation;
    private String purchaserName;
    private Instant purchaseTime;
    private boolean isUsed;
}
