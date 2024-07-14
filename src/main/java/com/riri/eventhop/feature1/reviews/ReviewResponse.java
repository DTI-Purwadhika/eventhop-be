package com.riri.eventhop.feature1.reviews;

import lombok.Data;
import java.time.Instant;

@Data
public class ReviewResponse {
    private String userName;
    private Integer rating;
    private String comment;
    private String eventName;
    private Instant createdAt;
}