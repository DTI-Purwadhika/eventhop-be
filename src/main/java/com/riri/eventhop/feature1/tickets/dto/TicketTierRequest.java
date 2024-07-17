package com.riri.eventhop.feature1.tickets.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketTierRequest {
    @NotBlank(message = "Ticket tier name must not be blank")
    private String name;

    @NotNull(message = "Price must be provided")
    @Min(value = 0, message = "Price must be greater than or equal to {value}")
    private BigDecimal price;

    @NotNull(message = "Free status must be provided")
    private Boolean isFree;

    @NotNull(message = "Quota must be provided")
    @Min(value = 0, message = "Quota must be greater than or equal to {value}")
    private Integer quota;
}
