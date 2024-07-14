package com.riri.eventhop.feature1.events.dto;

import com.riri.eventhop.feature1.events.entity.EventCategory;
import com.riri.eventhop.feature2.users.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class EventSummaryResponse implements Serializable{
    private Long id;
    private String title;
    private EventCategory eventCategory;
    private String imageUrl;
    private Instant startTime;
    private String location;
    private BigDecimal price;
    private Boolean isFree;
    private String organizer;

}
