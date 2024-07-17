package com.riri.eventhop.feature1.events.dto;

import com.riri.eventhop.feature1.events.entity.EventCategory;
import com.riri.eventhop.feature1.tickets.dto.TicketTierResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDetailsResponse implements Serializable{
    private Long id;
    private String title;
    private String code;
    private EventCategory eventCategory;
    private String description;
    private String imageUrl;
    private String address;
    private String location;
    private Instant startTime;
    private Instant endTime;
//    private BigDecimal price;
//    private Boolean isFree;
    private Integer availableSeats;
    private String eventUrl;
    private String organizer;
    private List<TicketTierResponse> ticketTiers;


//    private Instant createdAt;
//    private Instant updatedAt;
//    private Set<Instant> eventTimeslot;
}
