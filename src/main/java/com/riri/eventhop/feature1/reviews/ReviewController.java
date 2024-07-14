package com.riri.eventhop.feature1.reviews;

import com.riri.eventhop.response.PageResponse;
import com.riri.eventhop.response.Response;
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
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Page<ReviewResponse> reviewsPage = reviewService.getReviewsByEventId(eventId,
                PaginationUtil.createPageable(page, limit, sortDirection, sortBy));

        PageResponse<ReviewResponse> pageResponse = new PageResponse<>(
                reviewsPage.getContent(),
                reviewsPage.getNumber(),
                reviewsPage.getSize(),
                reviewsPage.getTotalElements(),
                reviewsPage.getTotalPages(),
                reviewsPage.isFirst(),
                reviewsPage.isLast()
        );

        return Response.success("Reviews for event fetched successfully", pageResponse);
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
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Page<ReviewResponse> reviewsPage = reviewService.getReviewsByOrganizerId(organizerId,
                PaginationUtil.createPageable(page, limit, sortDirection, sortBy));

        PageResponse<ReviewResponse> pageResponse = new PageResponse<>(
                reviewsPage.getContent(),
                reviewsPage.getNumber(),
                reviewsPage.getSize(),
                reviewsPage.getTotalElements(),
                reviewsPage.getTotalPages(),
                reviewsPage.isFirst(),
                reviewsPage.isLast()
        );

        return Response.success("Reviews for organizer fetched successfully", pageResponse);
    }
}