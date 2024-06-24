//package com.riri.eventhop.events.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//
//@Entity
//@Data
//@Table(name = "promotions")
//public class Promotion {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "event_id")
//    private Event event;
//
//    private String code;
//    private BigDecimal discountAmount;
//    private Integer maxUses;
//    private Integer usedCount;
//    private Instant expirationDate;
//}
