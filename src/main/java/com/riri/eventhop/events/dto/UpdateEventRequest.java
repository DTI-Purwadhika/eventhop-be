package com.riri.eventhop.events.dto;

import com.riri.eventhop.events.entity.EventCategory;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
public class UpdateEventRequest  implements Serializable{
    private String title;
    private String code;
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
}
