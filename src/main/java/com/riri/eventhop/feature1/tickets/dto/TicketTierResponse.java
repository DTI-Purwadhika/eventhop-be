package com.riri.eventhop.feature1.tickets.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class TicketTierResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Boolean isFree;
    private Integer quota;
    private Integer remainingQuota;
}