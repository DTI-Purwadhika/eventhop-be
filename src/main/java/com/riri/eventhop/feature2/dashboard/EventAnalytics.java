package com.riri.eventhop.feature2.dashboard;

import com.riri.eventhop.feature1.events.entity.Event;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
@Table(name = "event_analytics")
public class EventAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    private AnalyticsPeriod period;

    private Instant date;
    private BigDecimal revenue;
    private Integer attendeeCount;
    private Double averageRating;

}