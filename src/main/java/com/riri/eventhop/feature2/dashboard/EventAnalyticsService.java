package com.riri.eventhop.feature2.dashboard;

import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.repository.EventRepository;
import com.riri.eventhop.feature1.reviews.ReviewRepository;
import com.riri.eventhop.feature1.tickets.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventAnalyticsService {
    private final EventAnalyticsRepository eventAnalyticsRepository;
    private final TicketRepository ticketRepository;
    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;

    @Transactional
    public void generateAnalyticsForEvent(Long eventId, Instant startDate, Instant endDate) {
        log.info("Manually generating analytics for event {} from {} to {}", eventId, startDate, endDate);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Generate daily analytics
        Instant currentDate = startDate;
        while (currentDate.isBefore(endDate) || currentDate.equals(endDate)) {
            generateDailyAnalytics(event, currentDate);
            currentDate = currentDate.plus(1, ChronoUnit.DAYS);
        }

        // Generate weekly analytics
        Instant weekStart = startDate;
        while (weekStart.isBefore(endDate) || weekStart.equals(endDate)) {
            Instant weekEnd = weekStart.plus(6, ChronoUnit.DAYS);
            if (weekEnd.isAfter(endDate)) {
                weekEnd = endDate;
            }
            generatePeriodAnalytics(event, AnalyticsPeriod.WEEKLY, weekStart, weekEnd);
            weekStart = weekStart.plus(7, ChronoUnit.DAYS);
        }

        // Generate monthly analytics
        Instant monthStart = startDate.atZone(ZoneId.systemDefault()).withDayOfMonth(1).toInstant();
        while (monthStart.isBefore(endDate) || monthStart.equals(endDate)) {
            Instant monthEnd = monthStart.atZone(ZoneId.systemDefault()).plusMonths(1).minusDays(1).toInstant();
            if (monthEnd.isAfter(endDate)) {
                monthEnd = endDate;
            }
            generatePeriodAnalytics(event, AnalyticsPeriod.MONTHLY, monthStart, monthEnd);
            monthStart = monthStart.atZone(ZoneId.systemDefault()).plusMonths(1).withDayOfMonth(1).toInstant();
        }

        // Generate yearly analytics if the range spans multiple years
        int startYear = startDate.atZone(ZoneId.systemDefault()).getYear();
        int endYear = endDate.atZone(ZoneId.systemDefault()).getYear();
        if (startYear != endYear) {
            for (int year = startYear; year <= endYear; year++) {
                Instant yearStart = Instant.from(ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
                Instant yearEnd = Instant.from(ZonedDateTime.of(year, 12, 31, 23, 59, 59, 999999999, ZoneId.systemDefault()));
                if (yearStart.isBefore(startDate)) {
                    yearStart = startDate;
                }
                if (yearEnd.isAfter(endDate)) {
                    yearEnd = endDate;
                }
                generatePeriodAnalytics(event, AnalyticsPeriod.YEARLY, yearStart, yearEnd);
            }
        }

        log.info("Analytics generation completed for event {}", eventId);
    }

    private void generateDailyAnalytics(Event event, Instant date) {
        Instant nextDay = date.plus(1, ChronoUnit.DAYS);
        BigDecimal revenue = calculateRevenue(event.getId(), date, nextDay);
        int attendeeCount = calculateAttendeeCount(event.getId(), date, nextDay);
        double averageRating = calculateAverageRating(event.getId(), date, nextDay);

        saveEventAnalytics(event, AnalyticsPeriod.DAILY, date, revenue, attendeeCount, averageRating);
    }

    private void generatePeriodAnalytics(Event event, AnalyticsPeriod period, Instant startDate, Instant endDate) {
        BigDecimal revenue = calculateRevenue(event.getId(), startDate, endDate);
        int attendeeCount = calculateAttendeeCount(event.getId(), startDate, endDate);
        double averageRating = calculateAverageRating(event.getId(), startDate, endDate);

        saveEventAnalytics(event, period, endDate, revenue, attendeeCount, averageRating);
    }

    private BigDecimal calculateRevenue(Long eventId, Instant startDate, Instant endDate) {
        return ticketRepository.sumRevenueByEventIdAndDateRange(eventId, startDate, endDate);
    }

    private int calculateAttendeeCount(Long eventId, Instant startDate, Instant endDate) {
        Integer count = ticketRepository.countAttendeesByEventIdAndDateRange(eventId, startDate, endDate);
        return count != null ? count : 0;
    }

    private double calculateAverageRating(Long eventId, Instant startDate, Instant endDate) {
        Double averageRating = reviewRepository.averageRatingByEventIdAndDateRange(eventId, startDate, endDate);
        return averageRating != null ? averageRating : 0.0;
    }

    private void saveEventAnalytics(Event event, AnalyticsPeriod period, Instant date,
                                    BigDecimal revenue, int attendeeCount, double averageRating) {
        EventAnalytics analytics = new EventAnalytics();
        analytics.setEvent(event);
        analytics.setPeriod(period);
        analytics.setDate(date);
        analytics.setRevenue(revenue);
        analytics.setAttendeeCount(attendeeCount);
        analytics.setAverageRating(averageRating);

        eventAnalyticsRepository.save(analytics);
    }

    public List<EventAnalytics> getEventAnalytics(Long eventId, AnalyticsPeriod period, Instant startDate, Instant endDate) {
        log.info("Fetching analytics for event {} from {} to {} with period {}", eventId, startDate, endDate, period);

        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }

        List<EventAnalytics> analyticsData = eventAnalyticsRepository.findByEventIdAndPeriodAndDateBetween(
                eventId, period, startDate, endDate);

        log.info("Found {} analytics entries for event {}", analyticsData.size(), eventId);

        return analyticsData;
    }
}