package com.riri.eventhop.feature1.events.entity;

import com.riri.eventhop.feature1.promotions.Promotion;
import com.riri.eventhop.feature1.reviews.Review;
import com.riri.eventhop.feature1.tickets.Ticket;
import com.riri.eventhop.feature1.transactions.Transaction;
import com.riri.eventhop.feature2.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@Table(name = "events", indexes = {
        @Index(name = "idx_start_time", columnList = "start_time"),
        @Index(name = "idx_event_category", columnList = "event_category"),
        @Index(name = "idx_location", columnList = "locations")
})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Code must not be blank")
    @Column(unique = true)
    private String code;

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotNull(message = "Event category must be provided")
    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory;

    @NotBlank(message = "Description must not be blank")
    @Size(min = 10, max = 1000, message = "Description must be between {min} and {max} characters")
    private String description;

    @NotBlank(message = "Image URL must not be blank")
    @URL(message = "URL format is invalid")
    private String imageUrl;

    @NotBlank(message = "Address must not be blank")
    private String address;

    @NotBlank(message = "Location must not be blank")
    @Column(name = "locations")
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
    private String eventUrl;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Promotion> promotions;



    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
