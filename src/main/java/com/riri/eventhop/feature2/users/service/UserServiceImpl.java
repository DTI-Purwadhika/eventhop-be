package com.riri.eventhop.feature2.users.service;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.feature2.users.UserRepository;
import com.riri.eventhop.feature2.users.dto.RegisterRequest;
import com.riri.eventhop.feature2.users.entity.Discount;
import com.riri.eventhop.feature2.users.entity.Point;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.feature2.users.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException("Email is already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.addRole(UserRole.USER);  // Always set to USER role
        user.setReferralCode(generateReferralCode());

        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            User referrer = userRepository.findByReferralCode(request.getReferralCode())
                    .orElseThrow(() -> new ApplicationException("Invalid referral code"));

            user.setReferrer(referrer);
            addPointsToReferrer(referrer);
            addDiscountToNewUser(user);
        }

        return userRepository.save(user);
    }

    private String generateReferralCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    @Transactional
    private void addPointsToReferrer(User referrer) {
        Point point = new Point();
        point.setUser(referrer);
        point.setPoints(10000);
        point.setExpiryDate(LocalDateTime.now().plusMonths(3));
        referrer.getPoints().add(point);
        userRepository.save(referrer);
    }

    private void addDiscountToNewUser(User user) {
        Discount discount = new Discount();
        discount.setUser(user);
        discount.setDiscountPercentage(10);
        discount.setExpiryDate(LocalDateTime.now().plusMonths(3));
        user.getDiscounts().add(discount);
    }
    @Override
    public Integer calculateAvailablePoints(User user) {
        if (user == null) {
            log.warn("Attempted to calculate points for null user");
            return 0;
        }
        return user.getPoints().stream()
                .filter(p -> p.getExpiryDate().isAfter(LocalDateTime.now()))
                .mapToInt(Point::getPoints)
                .sum();
    }
    @Override
    public Discount getAvailableReferralDiscount(User user) {
        return user.getDiscounts().stream()
                .filter(d -> d.getExpiryDate().isAfter(LocalDateTime.now()) && !d.isUsed())
                .findFirst()
                .orElse(null);
    }


    @Override
    @Transactional
    public void useReferralDiscount(User user) {
        Discount referralDiscount = getAvailableReferralDiscount(user);
        if (referralDiscount != null) {
            referralDiscount.setUsed(true);
            userRepository.save(user);
        }
    }
    @Override
    @Transactional
    public void redeemPoints(User user, int pointsToRedeem) {
        int availablePoints = calculateAvailablePoints(user);
        if (pointsToRedeem > availablePoints) {
            throw new ApplicationException("Not enough points available");
        }

        List<Point> points = user.getPoints().stream()
                .filter(p -> p.getExpiryDate().isAfter(LocalDate.now().atStartOfDay()))
                .sorted(Comparator.comparing(Point::getExpiryDate))
                .toList();

        int remainingPointsToRedeem = pointsToRedeem;
        for (Point point : points) {
            if (remainingPointsToRedeem <= 0) break;
            if (point.getPoints() <= remainingPointsToRedeem) {
                remainingPointsToRedeem -= point.getPoints();
                point.setPoints(0);
            } else {
                point.setPoints(point.getPoints() - remainingPointsToRedeem);
                remainingPointsToRedeem = 0;
            }
        }

        user.getPoints().removeIf(p -> p.getPoints() == 0);
        userRepository.save(user);
    }
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
    @Override
    public List<User> findReferredUsers(User referrer) {
        return userRepository.findByReferrer(referrer);
    }
}