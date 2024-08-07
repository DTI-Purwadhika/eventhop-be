package com.riri.eventhop.feature1.events.dto;

import com.riri.eventhop.feature1.events.entity.EventCategory;
import com.riri.eventhop.util.CustomPageable;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
@Data
public class GetAllEventsParams {
    private EventCategory category;
    private String filter;
    private BigDecimal minTicketPrice;
    private BigDecimal maxTicketPrice;
    private Instant fromDate;
    private Instant untilDate;
    private String location;
    private Long userId;
    private Boolean isFree;
    private Long organizerId;
    private String organizer;
    private CustomPageable customPageable; // Use CustomPageable from PaginationUtil
}