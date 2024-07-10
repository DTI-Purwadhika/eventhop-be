package com.riri.eventhop.feature2.users.controller;


import com.riri.eventhop.feature2.users.service.UserService;
import com.riri.eventhop.feature2.users.dto.DiscountResponse;
import com.riri.eventhop.feature2.users.dto.PointResponse;
import com.riri.eventhop.feature2.users.dto.UserInfoDTO;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<UserInfoDTO> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        log.info("Received request for user info. JWT: {}", jwt);
        if (jwt == null) {
            log.warn("JWT is null in getUserInfo method");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwt.getClaimAsString("sub");
        User user = userService.findByEmail(email);
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
        User user = userService.findByEmail(email);

        if (user == null) {
            log.warn("User not found for email: {}", email);
            return Response.failed(HttpStatus.NOT_FOUND.value(),"User not found" );
        }

        Integer points = userService.calculateAvailablePoints(user);
        log.info("Retrieved {} points for user {}", points, email);
        PointResponse pointResponse = new PointResponse(points);
        return Response.success("Points retrieved successfully", pointResponse);
    }

    @GetMapping("/discounts")
    public ResponseEntity<Response<DiscountResponse>> getAvailableDiscount(@AuthenticationPrincipal Jwt jwt) {
        log.info("Received request for discounts. JWT subject: {}", jwt.getSubject());

        String email = jwt.getSubject();
        User user = userService.findByEmail(email);

        if (user == null) {
            log.warn("User not found for email: {}", email);
            return Response.failed(HttpStatus.NOT_FOUND.value(),"User not found" );
        }
        Integer discount = userService.getAvailableDiscount(user);
        DiscountResponse discountResponse = new DiscountResponse(discount);
        return Response.success("Discount retrieved successfully", discountResponse);
    }
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
//
//        String token = UUID.randomUUID().toString();
//        createPasswordResetTokenForUser(user, token);
//
//        // Send email with reset link (token)
//        mailService.sendPasswordResetEmail(user.getEmail(), token);
//
//        return ResponseEntity.ok("Password reset link sent to email");
//    }
//
//    private void createPasswordResetTokenForUser(User user, String token) {
//        PasswordResetToken myToken = new PasswordResetToken(token, user);
//        passwordTokenRepository.save(myToken);
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
//        PasswordResetToken resetToken = passwordTokenRepository.findByToken(token)
//                .orElseThrow(() -> new InvalidTokenException("Invalid or expired password reset token"));
//
//        User user = resetToken.getUser();
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//
//        passwordTokenRepository.delete(resetToken);
//
//        return ResponseEntity.ok("Password has been reset successfully");
//    }
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
//        // Optional: Add token to a blacklist or invalidate it server-side
//        return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
//    }
}