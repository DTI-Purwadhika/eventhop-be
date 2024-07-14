package com.riri.eventhop.feature1.reviews;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer rating;
    private String comment;
}