package com.riri.eventhop.events.dto;

import com.riri.eventhop.events.entity.EventCategory;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;
import java.time.Instant;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class CreateEventRequest implements Serializable {
    @NotBlank(message = "Title must not be blank")
    private String title;
    @NotBlank(message = "Code must not be blank")
    @Column(unique = true)
    private String code;

    @NotNull(message = "Event category must be provided")
    private EventCategory eventCategory;

    @NotBlank(message = "Description must not be blank")
    @Size(min = 10, max = 500, message = "Description must be between {min} and {max} characters")
    private String description;

    @NotBlank(message = "Main image must not be blank")
    private String mainImage;
    private Set<String> imageUrls;

    @NotBlank(message = "Address must not be blank")
    private String address;

    @NotBlank(message = "Location must not be blank")
    private String location;

    @NotNull(message = "Start time must be provided")
    private Instant startTime;

    @NotNull(message = "End time must be provided")
    private Instant endTime;

    @NotNull(message = "Price must be provided")
    @Min(value = 0, message = "Price must be greater than or equal to {value}")
    private BigDecimal price;

    @NotNull(message = "Free status must be provided")
    private boolean isFree;

    @NotNull(message = "Available seats must be provided")
    @Min(value = 0, message = "Available seats must be greater than or equal to {value}")
    private Integer availableSeats;

    @URL(message = "URL format is invalid")
    private String url;
}
