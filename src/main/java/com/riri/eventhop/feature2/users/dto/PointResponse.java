package com.riri.eventhop.feature2.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PointResponse {
    private String description;
    private int availablePoints;
    private LocalDateTime receivedDate;
}
