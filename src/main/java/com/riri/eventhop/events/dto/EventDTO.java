package com.riri.eventhop.events.dto;

import com.riri.eventhop.events.enums.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class EventDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String address;
    private Instant startTime;
    private Instant endTime;
    private BigDecimal price;
    private Integer availableSeats;
    private String mainImage;
    private Category category;
    private String location;
}