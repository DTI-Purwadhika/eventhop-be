package com.riri.eventhop.feature1.promotions.dto;

import com.riri.eventhop.feature1.promotions.PromotionType;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromotionResponse {

    private Long id;
    private String name;
    private String code;
    private PromotionType type;
    private BigDecimal amount;
    private Integer quota;
    private LocalDateTime expireDate;
    private String eventName;
    }
