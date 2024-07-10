package com.riri.eventhop.feature2.users.dto;

import com.riri.eventhop.feature2.users.entity.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DiscountResponse {
    private Integer discountPercentage;
    private boolean used;
    private LocalDateTime expiryDate;
}
