package com.riri.eventhop.events.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
public class EventDetailsDTO extends EventSummaryDTO implements Serializable{
    private String description;
    private Set<String> imageUrls;
    private String address;
    private Instant endTime;
    private Integer availableSeats;
    private String url;
}
//private Set<Instant> eventTimeslot;