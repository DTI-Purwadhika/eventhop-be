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
//@Table(name = "transactions")
//public class Transaction {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "event_id")
//    private Event event;
//
//    private String attendeeName;
//    private String attendeeEmail;
//    private BigDecimal amount;
//    private Instant transactionDate;
//}