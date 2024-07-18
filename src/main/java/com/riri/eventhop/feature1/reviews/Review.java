package com.riri.eventhop.feature1.reviews;

import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature2.users.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double rating;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private Instant createdAt;
}
