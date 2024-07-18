package com.riri.eventhop.config;

import com.riri.eventhop.feature1.promotions.Promotion;
import com.riri.eventhop.feature1.promotions.PromotionRepository;
import com.riri.eventhop.feature2.users.UserRepository;
import com.riri.eventhop.feature2.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;

    @Scheduled(cron = "0 0 0 * * *")  // Run daily at midnight
    @Transactional
    public void cleanupExpiredPointsAndDiscounts() {
        LocalDate now = LocalDate.now();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            // Clean up expired points
            user.getPoints().removeIf(point -> point.getExpiryDate().toLocalDate().isBefore(now) || point.getExpiryDate().toLocalDate().isEqual(now));

            // Clean up expired discounts
            user.getDiscounts().removeIf(discount -> discount.getExpiryDate().toLocalDate().isBefore(now) || discount.getExpiryDate().toLocalDate().isEqual(now));

        }

        userRepository.saveAll(users);
    }
}