package com.riri.eventhop.feature2.dashboard;

import com.riri.eventhop.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class EventAnalyticsController {
    private final EventAnalyticsService eventAnalyticsService;

    @PostMapping("/events/{eventId}/generate")
    public ResponseEntity<Response<String>> generateEventAnalytics(
            @PathVariable Long eventId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        eventAnalyticsService.generateAnalyticsForEvent(eventId, startDate, endDate);
        return Response.success("Analytics generation triggered successfully");
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Response<List<EventAnalyticsResponse>>> getEventAnalytics(
            @PathVariable Long eventId,
            @RequestParam AnalyticsPeriod period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        List<EventAnalytics> analytics = eventAnalyticsService.getEventAnalytics(eventId, period, startInstant, endInstant);
        List<EventAnalyticsResponse> response = mapToEventAnalyticsResponse(analytics);
        return Response.success("Event analytics fetched successfully", response);
    }

    private List<EventAnalyticsResponse> mapToEventAnalyticsResponse(List<EventAnalytics> analytics) {
        return analytics.stream()
                .map(this::mapSingleEventAnalytics)
                .collect(Collectors.toList());
    }

    private EventAnalyticsResponse mapSingleEventAnalytics(EventAnalytics analytics) {
        EventAnalyticsResponse response = new EventAnalyticsResponse();
        response.setEventId(analytics.getEvent().getId());
        response.setEventTitle(analytics.getEvent().getTitle());
        response.setPeriod(analytics.getPeriod());
        response.setDate(analytics.getDate().atZone(ZoneOffset.UTC).toLocalDate());
        response.setRevenue(analytics.getRevenue());
        response.setAttendeeCount(analytics.getAttendeeCount());
        response.setAverageRating(analytics.getAverageRating());
        return response;
    }
}