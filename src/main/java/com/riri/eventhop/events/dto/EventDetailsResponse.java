package com.riri.eventhop.events.dto;

import com.riri.eventhop.events.entity.EventCategory;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
public class EventDetailsResponse implements Serializable{
    private Long id;
    private String code;
    private String title;
    private EventCategory eventCategory;
    private String description;
    private String mainImage;
    private Set<String> imageUrls;
    private String address;
    private String location;
    private Instant startTime;
    private Instant endTime;
    private BigDecimal price;
    private boolean isFree;
    private Integer availableSeats;
    private String url;
//    private Instant createdAt;
//    private Instant updatedAt;
//    private Set<Instant> eventTimeslot;
}
