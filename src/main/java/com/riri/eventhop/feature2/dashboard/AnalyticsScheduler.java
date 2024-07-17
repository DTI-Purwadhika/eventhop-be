package com.riri.eventhop.feature2.dashboard;

import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsScheduler {

    private final EventAnalyticsService eventAnalyticsService;
    private final EventRepository eventRepository;


    @Scheduled(cron = "0 0 * * * ?") // Every hour at the start of the hour
    public void generateHourlyAnalytics() {
        log.info("Starting hourly analytics generation");
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        generateAnalytics(oneHourAgo, Instant.now(), AnalyticsPeriod.HOURLY);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void generateDailyAnalytics() {
        log.info("Starting daily analytics generation");
        Instant oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS);
        generateAnalytics(oneDayAgo, Instant.now(), AnalyticsPeriod.DAILY);
    }

    @Scheduled(cron = "0 0 0 * * MON") // Every Monday at midnight
    public void generateWeeklyAnalytics() {
        log.info("Starting weekly analytics generation");
        Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        generateAnalytics(oneWeekAgo, Instant.now(), AnalyticsPeriod.WEEKLY);
    }

    @Scheduled(cron = "0 0 0 1 * ?") // 1st day of every month at midnight
    public void generateMonthlyAnalytics() {
        log.info("Starting monthly analytics generation");
        Instant oneMonthAgo = Instant.now().minus(1, ChronoUnit.MONTHS);
        generateAnalytics(oneMonthAgo, Instant.now(), AnalyticsPeriod.MONTHLY);
    }

    @Scheduled(cron = "0 0 0 1 1 ?") // January 1st at midnight
    public void generateYearlyAnalytics() {
        log.info("Starting yearly analytics generation");
        Instant oneYearAgo = Instant.now().minus(1, ChronoUnit.YEARS);
        generateAnalytics(oneYearAgo, Instant.now(), AnalyticsPeriod.YEARLY);
    }

    private void generateAnalytics(Instant from, Instant to, AnalyticsPeriod period) {
        try {
            List<Event> events = eventRepository.findAll();
            for (Event event : events) {
                eventAnalyticsService.generateAnalyticsForEvent(event.getId(), from, to, period);
            }
            log.info("Completed {} analytics generation", period);
        } catch (Exception e) {
            log.error("Error generating {} analytics", period, e);
        }
    }
}