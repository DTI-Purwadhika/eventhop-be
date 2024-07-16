package com.riri.eventhop.feature2.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class EventAnalyticsResponse {
    private Long eventId;
    private String eventTitle;
    private AnalyticsPeriod period;
    private LocalDate date;
    private BigDecimal revenue;
    private Integer attendeeCount;
    private Double averageRating;

}