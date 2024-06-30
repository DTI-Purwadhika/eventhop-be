package com.riri.eventhop.events.dto;

import com.riri.eventhop.events.entity.EventCategory;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class EventSummaryDTO implements Serializable{
    private Long id;
    private String code;
    private String title;
    private EventCategory eventCategory;
    private String mainImage;
    private String location;
    private Instant startTime;
    private BigDecimal price;
    private boolean isFree;
}