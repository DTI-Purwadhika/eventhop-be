package com.riri.eventhop.feature1.tickets;

import com.riri.eventhop.feature1.events.entity.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "ticket_tiers")
public class TicketTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ticket tier name must not be blank")
    private String name;

    @NotNull(message = "Price must be provided")
    @Min(value = 0, message = "Price must be greater than or equal to {value}")
    private BigDecimal price;

    @NotNull(message = "Quota must be provided")
    @Min(value = 0, message = "Quota must be greater than or equal to {value}")
    private Integer quota;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @NotNull(message = "Remaining quota must be provided")
    @Min(value = 0, message = "Remaining quota must be greater than or equal to {value}")
    private Integer remainingQuota;

    public void decreaseQuota(int amount) {
        if (this.remainingQuota < amount) {
            throw new IllegalStateException("Not enough tickets available in this tier");
        }
        this.remainingQuota -= amount;
    }

    @PrePersist
    protected void onCreate() {
        if (this.remainingQuota == null) {
            this.remainingQuota = this.quota;
        }
    }
}