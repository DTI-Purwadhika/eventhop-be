//package com.riri.eventhop.events.dto;
//
//import com.riri.eventhop.events.entity.EventCategory;
//import jakarta.persistence.Column;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.Data;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.time.Instant;
//
//@Data
//public class EventSummaryRequest implements Serializable{
//    @NotBlank(message = "Title must not be blank")
//    private String title;
//
//    @NotBlank(message = "Code must not be blank")
//    @Column(unique = true)
//    private String code;
//
//    @NotNull(message = "Event category must be provided")
//    private EventCategory eventCategory;
//
//    @NotBlank(message = "Main image must not be blank")
//    private String mainImage;
//
//    @NotBlank(message = "Location must not be blank")
//    private String location;
//
//    @NotNull(message = "Start time must be provided")
//    private Instant startTime;
//
//    @NotNull(message = "Price must be provided")
//    @Min(value = 0, message = "Price must be greater than or equal to {value}")
//    private BigDecimal price;
//
//    @NotNull(message = "Free status must be provided")
//    private boolean isFree;
//
//    @NotNull(message = "Available seats must be provided")
//    @Min(value = 0, message = "Available seats must be greater than or equal to {value}")
//    private Integer availableSeats;
//
//}