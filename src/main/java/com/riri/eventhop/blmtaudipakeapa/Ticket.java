//package com.riri.eventhop.tickets.entity;
//
//import com.riri.eventhop.events.entity.Event;
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.math.BigDecimal;
//
//
//@Entity
//@Data
//@Table(name = "tickets")
//public class Ticket {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "event_id")
//    private Event event;
//
//    private String type;
//    private BigDecimal price;
//    private Integer quantity;
//}
//
