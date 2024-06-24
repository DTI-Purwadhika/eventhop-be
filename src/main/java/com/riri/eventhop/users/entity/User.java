package com.riri.eventhop.users.entity;

import com.riri.eventhop.events.entity.Event;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String clerkId;
    private String name;
    private String email;
    private String imageUrl;
    private String referralCode;
    private Long points;
    private Instant pointsExpiryDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @Column(name = "organizer_name")
    private String organizerName;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Event> organizedEvents = new HashSet<>();

    public boolean isOrganizer() {
        return roles.contains("ORGANIZER");
    }
}