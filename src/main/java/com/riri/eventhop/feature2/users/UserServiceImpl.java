package com.riri.eventhop.feature2.users;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.feature2.referrals.discounts.Discount;
import com.riri.eventhop.feature2.referrals.points.Point;
import com.riri.eventhop.feature2.users.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
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

            addPointsToReferrer(referrer);
            addDiscountToNewUser(user);
        }

        return userRepository.save(user);
    }

    private String generateReferralCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

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

    public int calculateAvailablePoints(User user) {
        return user.getPoints().stream()
                .filter(p -> p.getExpiryDate().isAfter(LocalDateTime.now()))
                .mapToInt(Point::getPoints)
                .sum();
    }

    public double getAvailableDiscount(User user) {
        return user.getDiscounts().stream()
                .filter(d -> d.getExpiryDate().isAfter(LocalDateTime.now()))
                .mapToDouble(Discount::getDiscountPercentage)
                .findFirst()
                .orElse(0);
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
}