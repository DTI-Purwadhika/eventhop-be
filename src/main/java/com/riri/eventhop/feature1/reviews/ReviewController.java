package com.riri.eventhop.feature1.reviews;

import com.riri.eventhop.response.PageResponse;
import com.riri.eventhop.response.Response;
import com.riri.eventhop.util.CustomPageable;
import com.riri.eventhop.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/events/{eventId}")
    public ResponseEntity<Response<ReviewResponse>> createReview(
            @PathVariable Long eventId,
            @RequestBody ReviewRequest reviewRequest) {
        ReviewResponse createdReview = reviewService.createReview(eventId, reviewRequest);
        return Response.success("Review created successfully", createdReview);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Response<PageResponse<ReviewResponse>>> getReviewsByEventId(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "DESC") String order,
            @RequestParam(defaultValue = "createdAt") String sort) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, order, sort);
        Page<ReviewResponse> reviewsPage = reviewService.getReviewsByEventId(eventId, pageable);

        return Response.success("Reviews for event fetched successfully", PaginationUtil.createPageResponse(reviewsPage));
    }

    @GetMapping("/events/{eventId}/average-rating")
    public ResponseEntity<Response<Double>> getAverageRatingForEvent(@PathVariable Long eventId) {
        double averageRating = reviewService.getAverageRatingForEvent(eventId);
        return Response.success("Average rating fetched successfully", averageRating);
    }

    @GetMapping("/organizers/{organizerId}")
    public ResponseEntity<Response<PageResponse<ReviewResponse>>> getReviewsByOrganizerId(
            @PathVariable Long organizerId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "DESC") String order,
            @RequestParam(defaultValue = "createdAt") String sort) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, order, sort);
        Page<ReviewResponse> reviewsPage = reviewService.getReviewsByOrganizerId(organizerId, pageable);

        return Response.success("Reviews for organizer fetched successfully", PaginationUtil.createPageResponse(reviewsPage));
    }
}