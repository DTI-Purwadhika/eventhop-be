package com.riri.eventhop.feature1.promotions.dto;

import com.riri.eventhop.feature1.promotions.PromotionType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromotionRequest {

    @NotBlank(message = "Promotion name cannot be empty")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "Promotion code cannot be empty")
    private String code;

    @NotNull(message = "Promotion type cannot be null")
    private PromotionType type;

    @Min(value = 0, message = "Promotion amount must be greater than or equal to 0")
    private BigDecimal amount;

    @Min(value = 1, message = "Promotion quota must be greater than or equal to 1")
    private Integer quota;

    @NotNull(message = "Promotion expire date cannot be null")
    @Future(message = "Promotion expire date must be in the future")
    private LocalDateTime expireDate;

    @NotNull(message = "Promotion start date cannot be null")
    @FutureOrPresent(message = "Promotion start date must be now or in the future")
    private LocalDateTime startDate;

}
