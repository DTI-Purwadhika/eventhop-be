package com.riri.eventhop.feature2.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface EventAnalyticsRepository extends JpaRepository<EventAnalytics, Long> {
    List<EventAnalytics> findByEventIdAndPeriodAndDateBetween(
            Long eventId, AnalyticsPeriod period, Instant startDate, Instant endDate);
}
