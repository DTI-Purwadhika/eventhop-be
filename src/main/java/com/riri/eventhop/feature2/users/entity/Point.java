package com.riri.eventhop.feature2.users.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;

    private Integer points;

    private LocalDateTime createdAt;

    private LocalDateTime expiryDate;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}