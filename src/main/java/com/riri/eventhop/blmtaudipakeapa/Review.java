//package com.riri.eventhop.reviews.entity;
//
//import com.riri.eventhop.events.entity.Event;
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.Instant;
//
//@Entity
//@Data
//@Table(name = "reviews")
//public class Review {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "event_id")
//    private Event event;
//
//    private Integer rating;
//    private String comment;
//    private Instant createdAt;
//}
