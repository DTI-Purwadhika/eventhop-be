package com.riri.eventhop.feature2.dashboard;

import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.repository.EventRepository;
import com.riri.eventhop.feature1.reviews.ReviewRepository;
import com.riri.eventhop.feature1.tickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        log.info("Manually generating all analytics for event {} from {} to {}", eventId, startDate, endDate);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        generateHourlyAnalytics(event, startDate, endDate);
        generateDailyAnalytics(event, startDate, endDate);
        generateWeeklyAnalytics(event, startDate, endDate);
        generateMonthlyAnalytics(event, startDate, endDate);
        generateYearlyAnalytics(event, startDate, endDate);

        log.info("Analytics generation completed for event {}", eventId);
    }

    @Transactional
    public void generateAnalyticsForEvent(Long eventId, Instant startDate, Instant endDate, AnalyticsPeriod period) {
        log.info("Generating {} analytics for event {} from {} to {}", period, eventId, startDate, endDate);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        switch (period) {
            case HOURLY:
                generateHourlyAnalytics(event, startDate, endDate);
                break;
            case DAILY:
                generateDailyAnalytics(event, startDate, endDate);
                break;
            case WEEKLY:
                generateWeeklyAnalytics(event, startDate, endDate);
                break;
            case MONTHLY:
                generateMonthlyAnalytics(event, startDate, endDate);
                break;
            case YEARLY:
                generateYearlyAnalytics(event, startDate, endDate);
                break;
        }

        log.info("Completed {} analytics generation for event {}", period, eventId);
    }

    private void generateHourlyAnalytics(Event event, Instant startDate, Instant endDate) {
        Instant currentHour = startDate.truncatedTo(ChronoUnit.HOURS);
        while (currentHour.isBefore(endDate)) {
            Instant nextHour = currentHour.plus(1, ChronoUnit.HOURS);
            generatePeriodAnalytics(event, AnalyticsPeriod.HOURLY, currentHour, nextHour);
            currentHour = nextHour;
        }
    }

    private void generateDailyAnalytics(Event event, Instant startDate, Instant endDate) {
        Instant currentDay = startDate.truncatedTo(ChronoUnit.DAYS);
        while (currentDay.isBefore(endDate)) {
            Instant nextDay = currentDay.plus(1, ChronoUnit.DAYS);
            generatePeriodAnalytics(event, AnalyticsPeriod.DAILY, currentDay, nextDay);
            currentDay = nextDay;
        }
    }

    private void generateWeeklyAnalytics(Event event, Instant startDate, Instant endDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime currentWeekStart = startDate.atZone(zoneId).truncatedTo(ChronoUnit.DAYS)
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        while (currentWeekStart.toInstant().isBefore(endDate)) {
            ZonedDateTime nextWeekStart = currentWeekStart.plusWeeks(1);
            generatePeriodAnalytics(event, AnalyticsPeriod.WEEKLY, currentWeekStart.toInstant(), nextWeekStart.toInstant());
            currentWeekStart = nextWeekStart;
        }
    }

    private void generateMonthlyAnalytics(Event event, Instant startDate, Instant endDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime currentMonthStart = startDate.atZone(zoneId).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);

        while (currentMonthStart.toInstant().isBefore(endDate)) {
            ZonedDateTime nextMonthStart = currentMonthStart.plusMonths(1);
            generatePeriodAnalytics(event, AnalyticsPeriod.MONTHLY, currentMonthStart.toInstant(), nextMonthStart.toInstant());
            currentMonthStart = nextMonthStart;
        }
    }

    private void generateYearlyAnalytics(Event event, Instant startDate, Instant endDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime currentYearStart = startDate.atZone(zoneId).withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);

        while (currentYearStart.toInstant().isBefore(endDate)) {
            ZonedDateTime nextYearStart = currentYearStart.plusYears(1);
            generatePeriodAnalytics(event, AnalyticsPeriod.YEARLY, currentYearStart.toInstant(), nextYearStart.toInstant());
            currentYearStart = nextYearStart;
        }
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

        if (analyticsData.isEmpty()) {
            log.info("No analytics found. Generating analytics for the requested period.");
            generateAnalyticsForEvent(eventId, startDate, endDate, period);
            analyticsData = eventAnalyticsRepository.findByEventIdAndPeriodAndDateBetween(
                    eventId, period, startDate, endDate);
        }

        log.info("Found {} analytics entries for event {}", analyticsData.size(), eventId);

        return analyticsData;
    }
}
//@EnableAsync
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class EventAnalyticsService {
//    private final EventAnalyticsRepository eventAnalyticsRepository;
//    private final TicketRepository ticketRepository;
//    private final ReviewRepository reviewRepository;
//    private final EventRepository eventRepository;
//
//    @Async
//    @Transactional
//    public CompletableFuture<Void> generateAnalyticsForEvent(Long eventId, Instant startDate, Instant endDate) {
//        log.info("Manually generating all analytics for event {} from {} to {}", eventId, startDate, endDate);
//
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
//
//        generateHourlyAnalytics(event, startDate, endDate);
//        generateDailyAnalytics(event, startDate, endDate);
//        generateWeeklyAnalytics(event, startDate, endDate);
//        generateMonthlyAnalytics(event, startDate, endDate);
//        generateYearlyAnalytics(event, startDate, endDate);
//
//        log.info("Analytics generation completed for event {}", eventId);
//        return CompletableFuture.completedFuture(null);
//    }
//
//    @Async
//    @Transactional
//    public CompletableFuture<Void> generateAnalyticsForEvent(Long eventId, Instant startDate, Instant endDate, AnalyticsPeriod period) {
//        log.info("Generating {} analytics for event {} from {} to {}", period, eventId, startDate, endDate);
//
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
//
//        switch (period) {
//            case HOURLY:
//                generateHourlyAnalytics(event, startDate, endDate);
//                break;
//            case DAILY:
//                generateDailyAnalytics(event, startDate, endDate);
//                break;
//            case WEEKLY:
//                generateWeeklyAnalytics(event, startDate, endDate);
//                break;
//            case MONTHLY:
//                generateMonthlyAnalytics(event, startDate, endDate);
//                break;
//            case YEARLY:
//                generateYearlyAnalytics(event, startDate, endDate);
//                break;
//        }
//
//        log.info("Completed {} analytics generation for event {}", period, eventId);
//        return CompletableFuture.completedFuture(null);
//    }
//
//    private void generateHourlyAnalytics(Event event, Instant startDate, Instant endDate) {
//        Instant currentHour = startDate.truncatedTo(ChronoUnit.HOURS);
//        while (currentHour.isBefore(endDate)) {
//            Instant nextHour = currentHour.plus(1, ChronoUnit.HOURS);
//            generatePeriodAnalytics(event, AnalyticsPeriod.HOURLY, currentHour, nextHour);
//            currentHour = nextHour;
//        }
//    }
//
//    private void generateDailyAnalytics(Event event, Instant startDate, Instant endDate) {
//        Instant currentDay = startDate.truncatedTo(ChronoUnit.DAYS);
//        while (currentDay.isBefore(endDate)) {
//            Instant nextDay = currentDay.plus(1, ChronoUnit.DAYS);
//            generatePeriodAnalytics(event, AnalyticsPeriod.DAILY, currentDay, nextDay);
//            currentDay = nextDay;
//        }
//    }
//
//    private void generateWeeklyAnalytics(Event event, Instant startDate, Instant endDate) {
//        ZoneId zoneId = ZoneId.systemDefault();
//        ZonedDateTime currentWeekStart = startDate.atZone(zoneId).truncatedTo(ChronoUnit.DAYS)
//                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
//
//        while (currentWeekStart.toInstant().isBefore(endDate)) {
//            ZonedDateTime nextWeekStart = currentWeekStart.plusWeeks(1);
//            generatePeriodAnalytics(event, AnalyticsPeriod.WEEKLY, currentWeekStart.toInstant(), nextWeekStart.toInstant());
//            currentWeekStart = nextWeekStart;
//        }
//    }
//
//    private void generateMonthlyAnalytics(Event event, Instant startDate, Instant endDate) {
//        ZoneId zoneId = ZoneId.systemDefault();
//        ZonedDateTime currentMonthStart = startDate.atZone(zoneId).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
//
//        while (currentMonthStart.toInstant().isBefore(endDate)) {
//            ZonedDateTime nextMonthStart = currentMonthStart.plusMonths(1);
//            generatePeriodAnalytics(event, AnalyticsPeriod.MONTHLY, currentMonthStart.toInstant(), nextMonthStart.toInstant());
//            currentMonthStart = nextMonthStart;
//        }
//    }
//
//    private void generateYearlyAnalytics(Event event, Instant startDate, Instant endDate) {
//        ZoneId zoneId = ZoneId.systemDefault();
//        ZonedDateTime currentYearStart = startDate.atZone(zoneId).withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
//
//        while (currentYearStart.toInstant().isBefore(endDate)) {
//            ZonedDateTime nextYearStart = currentYearStart.plusYears(1);
//            generatePeriodAnalytics(event, AnalyticsPeriod.YEARLY, currentYearStart.toInstant(), nextYearStart.toInstant());
//            currentYearStart = nextYearStart;
//        }
//    }
//
//    private void generatePeriodAnalytics(Event event, AnalyticsPeriod period, Instant startDate, Instant endDate) {
//        BigDecimal revenue = calculateRevenue(event.getId(), startDate, endDate);
//        int attendeeCount = calculateAttendeeCount(event.getId(), startDate, endDate);
//        double averageRating = calculateAverageRating(event.getId(), startDate, endDate);
//
//        saveEventAnalytics(event, period, endDate, revenue, attendeeCount, averageRating);
//    }
//
//    private BigDecimal calculateRevenue(Long eventId, Instant startDate, Instant endDate) {
//        return ticketRepository.sumRevenueByEventIdAndDateRange(eventId, startDate, endDate);
//    }
//
//    private int calculateAttendeeCount(Long eventId, Instant startDate, Instant endDate) {
//        Integer count = ticketRepository.countAttendeesByEventIdAndDateRange(eventId, startDate, endDate);
//        return count != null ? count : 0;
//    }
//
//    private double calculateAverageRating(Long eventId, Instant startDate, Instant endDate) {
//        Double averageRating = reviewRepository.averageRatingByEventIdAndDateRange(eventId, startDate, endDate);
//        return averageRating != null ? averageRating : 0.0;
//    }
//    @Transactional
//    private void saveEventAnalytics(Event event, AnalyticsPeriod period, Instant date,
//                                    BigDecimal revenue, int attendeeCount, double averageRating) {
//        EventAnalytics analytics = new EventAnalytics();
//        analytics.setEvent(event);
//        analytics.setPeriod(period);
//        analytics.setDate(date);
//        analytics.setRevenue(revenue);
//        analytics.setAttendeeCount(attendeeCount);
//        analytics.setAverageRating(averageRating);
//
//        eventAnalyticsRepository.save(analytics);
//    }
//    @Transactional
//    public CompletableFuture<List<EventAnalytics>> getEventAnalytics(Long eventId, AnalyticsPeriod period, Instant startDate, Instant endDate) {
//        log.info("Fetching analytics for event {} from {} to {} with period {}", eventId, startDate, endDate, period);
//
//        if (!eventRepository.existsById(eventId)) {
//            throw new ResourceNotFoundException("Event not found with id: " + eventId);
//        }
//
//        List<EventAnalytics> analyticsData = eventAnalyticsRepository.findByEventIdAndPeriodAndDateBetween(
//                eventId, period, startDate, endDate);
//
//        if (analyticsData.isEmpty()) {
//            log.info("No analytics found. Generating analytics for the requested period.");
//            generateAnalyticsForEvent(eventId, startDate, endDate, period);
//            analyticsData = eventAnalyticsRepository.findByEventIdAndPeriodAndDateBetween(
//                    eventId, period, startDate, endDate);
//        }
//
//        log.info("Found {} analytics entries for event {}", analyticsData.size(), eventId);
//
//        return CompletableFuture.completedFuture(eventAnalyticsRepository.findByEventIdAndPeriodAndDateBetween(eventId, period, startDate, endDate));    }
//}
