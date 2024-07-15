package com.riri.eventhop.feature1.tickets.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class TicketTierResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quota;
    private Integer remainingQuota;

}