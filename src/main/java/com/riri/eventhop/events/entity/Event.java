package com.riri.eventhop.events.entity;

import com.riri.eventhop.events.enums.Category;
import com.riri.eventhop.users.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String description;
    private String address;
    private Instant startTime;
    private Instant endTime;
    private BigDecimal price;
    @Column(name = "available_seats")
    private Integer availableSeats;
    private String mainImage;
    @ElementCollection
    @CollectionTable(name = "event_images", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "image_url")
    private Set<String> imageUrls = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private Category category;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public void setOrganizer(User user) {
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

}
