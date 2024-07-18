package com.riri.eventhop.feature1.promotions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.riri.eventhop.feature1.events.entity.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "promotions")
public class Promotion {
    private static final BigDecimal MAX_PERCENTAGE = new BigDecimal(100);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Promotion name cannot be empty")
    private String name;

    @NotBlank(message = "Promotion code cannot be empty")
    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Promotion type cannot be null")
    private PromotionType type;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Min(value = 1, message = "Max uses must be at least 1")
    private Integer quota;

    @NotNull
    @Future(message = "End date must be in the future")
    private LocalDateTime expireDate;

    @NotNull
    @FutureOrPresent(message = "Start date must be now or in the future")
    private LocalDateTime startDate;

    @Min(value = 0, message = "Used count cannot be negative")
    private Integer usedCount = 0;

    // Add this constructor
    public Promotion(String name, String code, PromotionType type, BigDecimal amount, Integer quota, LocalDateTime expireDate, LocalDateTime startDate) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.amount = amount;
        this.quota = quota;
        this.expireDate = expireDate;
        this.startDate = startDate;
    }


}
