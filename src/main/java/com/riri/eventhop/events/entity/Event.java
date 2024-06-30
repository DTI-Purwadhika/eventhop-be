package com.riri.eventhop.events.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.riri.eventhop.users.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "events", indexes = {
        @Index(name = "idx_start_time", columnList = "start_time"),
        @Index(name = "idx_event_category", columnList = "event_category"),
        @Index(name = "idx_location", columnList = "locations")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Event implements Serializable {
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
    @Size(min = 10, max = 500, message = "Description must be between {min} and {max} characters")
    private String description;

    @NotBlank(message = "Main image must not be blank")
    private String mainImage;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_images", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "image_url")
    private Set<String> imageUrls = new HashSet<>();

    public Set<String> getImageUrls() {
        Hibernate.initialize(imageUrls); // Initialize lazily loaded collection
        return imageUrls;
    }

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
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    @JsonIgnore
    private User organizer;

    @CreatedDate
    private Instant createdAt;

    private Instant updatedAt;
    private Instant deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    public boolean isFree() {
        return price == null || BigDecimal.ZERO.compareTo(price) == 0;
    }
}
