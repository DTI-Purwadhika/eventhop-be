package com.riri.eventhop.feature1.reviews;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.repository.EventRepository;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.feature2.users.UserRepository;
import com.riri.eventhop.util.CustomPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ReviewResponse createReview(Long eventId, ReviewRequest reviewRequest) {
        String userEmail = getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Event not found"));

        if (reviewRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "You have already reviewed this event");
        }

        Review review = new Review();
        review.setEvent(event);
        review.setUser(user);
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setCreatedAt(Instant.now());

        Review savedReview = reviewRepository.save(review);
        return mapReviewToResponse(savedReview);
    }

    @Override
    public Page<ReviewResponse> getReviewsByEventId(Long eventId, CustomPageable pageable) {
        Page<Review> reviewsPage = reviewRepository.findAll(pageable.toPageRequest());
        List<ReviewResponse> reviewResponses = reviewsPage.getContent().stream()
                .map(this::mapReviewToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(reviewResponses, pageable.toPageRequest(), reviewsPage.getTotalElements());
    }
    @Override
    @PreAuthorize("hasRole('ORGANIZER')")
    public Page<ReviewResponse> getReviewsByOrganizerId(Long organizerId, CustomPageable pageable) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Organizer not found"));

        Page<Review> reviewsPage = reviewRepository.findByEventOrganizerIdOrderByCreatedAtDesc(organizerId, pageable.toPageRequest());

        List<ReviewResponse> reviewResponses = reviewsPage.getContent().stream()
                .map(this::mapReviewToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(reviewResponses, pageable.toPageRequest(), reviewsPage.getTotalElements());
    }

    @Override
    public double getAverageRatingForEvent(Long eventId) {
        List<Review> reviews = reviewRepository.findByEventId(eventId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }

    private ReviewResponse mapReviewToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setUserName(review.getUser().getName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setEventName(review.getEvent().getTitle());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }
        throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected authentication type");
    }
}