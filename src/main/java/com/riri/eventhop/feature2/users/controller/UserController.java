package com.riri.eventhop.feature2.users.controller;

import com.riri.eventhop.feature2.users.auth.AuthService;
import com.riri.eventhop.feature2.users.entity.Point;
import com.riri.eventhop.feature2.users.dto.*;
import com.riri.eventhop.feature2.users.dto.ReferredUser;
import com.riri.eventhop.feature2.users.entity.Discount;
import com.riri.eventhop.feature2.users.service.UserService;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final AuthService authService;
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<Response<RegisterResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest);

        RegisterResponse registerResponse = new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getReferralCode()
                // Add other fields as needed, but avoid sensitive information

        );

        return Response.success("User registered successfully", registerResponse);
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<UserInfoDTO> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        log.info("Received request for user info. JWT: {}", jwt);
        if (jwt == null) {
            log.warn("JWT is null in getUserInfo method");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwt.getClaimAsString("sub");
        User user = authService.findByEmail(email);
        if (user == null) {
            log.warn("User not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserInfoDTO userInfoDTO = UserInfoDTO.fromUser(user);
        return ResponseEntity.ok(userInfoDTO);
    }
    @GetMapping("/points")
    public ResponseEntity<Response<PointResponse>> getAvailablePoints(@AuthenticationPrincipal Jwt jwt) {
        log.info("Received request for points. JWT subject: {}", jwt.getSubject());

        String email = jwt.getSubject();
        User user = authService.findByEmail(email);

        if (user == null) {
            log.warn("User not found for email: {}", email);
            return Response.failed(HttpStatus.NOT_FOUND.value(), "User not found");
        }

        Integer points = userService.calculateAvailablePoints(user);

        Point latestPoint = user.getPoints().stream()
                .filter(p -> p.getExpiryDate().isAfter(LocalDateTime.now()))
                .max(Comparator.comparing(Point::getCreatedAt))
                .orElse(null);

        LocalDateTime receivedDate = latestPoint != null ? latestPoint.getCreatedAt() : null;
        String description = latestPoint != null ? latestPoint.getDescription() : null;

        log.info("Retrieved {} points for user {}", points, email);
        PointResponse pointResponse = new PointResponse(description, points, receivedDate);
        return Response.success("Points retrieved successfully", pointResponse);
    }

    @GetMapping("/discounts")
    public ResponseEntity<Response<DiscountResponse>> getAvailableDiscount(@AuthenticationPrincipal Jwt jwt) {
        log.info("Received request for discounts. JWT subject: {}", jwt.getSubject());

        String email = jwt.getSubject();
        User user = authService.findByEmail(email);

        if (user == null) {
            log.warn("User not found for email: {}", email);
            return Response.failed(HttpStatus.NOT_FOUND.value(), "User not found");
        }

        Discount discount = userService.getAvailableReferralDiscount(user);
        if (discount == null) {
            log.info("No available discounts found for user {}", email);
            return Response.success("No discounts available", null);
        }

        DiscountResponse discountResponse = new DiscountResponse(
                discount.getDiscountPercentage(),
                discount.isUsed(),
                discount.getExpiryDate()
        );

        return Response.success("Discount retrieved successfully", discountResponse);
    }
    @GetMapping("/referral-code")
    public ResponseEntity<Response<String>> getReferralCode(@AuthenticationPrincipal Jwt jwt) {
        log.info("Received request for referral code. JWT subject: {}", jwt.getSubject());

        String email = jwt.getSubject();
        User user = authService.findByEmail(email);

        if (user == null) {
            log.warn("User not found for email: {}", email);
            return Response.failed(HttpStatus.NOT_FOUND.value(), "User not found");
        }

        String referralCode = user.getReferralCode();
        return Response.success("Referral code retrieved successfully", referralCode);
    }
    @GetMapping("/referral-info")
    public ResponseEntity<Response<ReferralInfo>> getReferralInfo(@AuthenticationPrincipal Jwt jwt) {
        log.info("Received request for referral info. JWT subject: {}", jwt.getSubject());

        String email = jwt.getSubject();
        User user = authService.findByEmail(email);

        if (user == null) {
            log.warn("User not found for email: {}", email);
            return Response.failed(HttpStatus.NOT_FOUND.value(), "User not found");
        }

        String referralCode = user.getReferralCode();
        List<User> referredUsers = userService.findReferredUsers(user);
        List<ReferredUser> referredUser = referredUsers.stream()
                .map(ReferredUser::fromUser)
                .collect(Collectors.toList());

        ReferralInfo referralInfo = new ReferralInfo(referralCode, referredUser);

        return Response.success("Referral info retrieved successfully", referralInfo);
    }
}
