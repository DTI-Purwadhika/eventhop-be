package com.riri.eventhop.events.dto;

import com.riri.eventhop.events.enums.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class EventSummaryDTO {
    private Long id;
    private String code;
    private String name;
    private Instant startTime;
    private BigDecimal price;
    private String mainImage;
    private Category category;
    private String location;
    private boolean trending;
}