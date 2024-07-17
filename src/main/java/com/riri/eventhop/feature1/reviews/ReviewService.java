package com.riri.eventhop.feature1.reviews;

import com.riri.eventhop.util.CustomPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long eventId, ReviewRequest reviewRequest);
    Page<ReviewResponse> getReviewsByEventId(Long eventId, CustomPageable pageable);
    Page<ReviewResponse> getReviewsByOrganizerId(CustomPageable pageable);
    double getAverageRatingForEvent(Long eventId);

}