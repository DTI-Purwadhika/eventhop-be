package com.riri.eventhop.feature2.users.auth;

import com.riri.eventhop.feature2.users.UserRepository;
import com.riri.eventhop.feature2.users.entity.UserRole;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.util.JwtTokenProvider;
import com.riri.eventhop.feature2.users.dto.*;
import com.riri.eventhop.response.Response;
import com.riri.eventhop.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            String jwt = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getRoles().iterator().next().name().toLowerCase(),
                    user.getAvatarUrl(),
                    user.getReferralCode(),
                    jwt,
                    refreshToken
            );

            return Response.success("Login successful", jwtResponse);
        } catch (AuthenticationException e) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    @PostMapping("/login/oauth")
    public ResponseEntity<?> authenticateOAuth(@RequestBody OAuthLoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseGet(() -> createNewUser(request));

            String jwt = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getRoles().iterator().next().name().toLowerCase(),
                    user.getAvatarUrl(),
                    user.getReferralCode(),
                    jwt,
                    refreshToken
            );

            return Response.success("OAuth login successful", jwtResponse);
        } catch (Exception e) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "OAuth login failed: " + e.getMessage());
        }
    }

    private User createNewUser(OAuthLoginRequest request) {
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.addRole(UserRole.USER);
        try {
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create new user: " + e.getMessage());
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            tokenBlacklistService.blacklistToken(jwtToken);
            return Response.success("Logged out successfully", null);
        }
        return Response.failed(HttpStatus.BAD_REQUEST.value(),"Invalid token");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String userEmail) {
        User user = authService.findByEmail(userEmail);
        if (user == null) {
            throw new ApplicationException("User not found with email: " + userEmail);
        }
        String token = UUID.randomUUID().toString();
        authService.createPasswordResetTokenForUser(user, token);
        // Here you would send an email with the token
        // For now, we'll just return the token in the response
        return Response.success("Reset password token generated", token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestParam("password") String newPassword,
                                           @RequestParam("confirmPassword") String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new ApplicationException("Passwords do not match");
        }

        String result = authService.validatePasswordResetToken(token);
        if (result != null) {
            throw new ApplicationException("Invalid or expired token");
        }

        User user = authService.getUserByPasswordResetToken(token);
        if (user == null) {
            throw new ApplicationException("User not found");
        }

        // Validate password strength
        if (!isPasswordStrong(newPassword)) {
            throw new ApplicationException("Password does not meet strength requirements");
        }

        authService.changeUserPassword(user, newPassword);

        // Invalidate the used token
        authService.invalidatePasswordResetToken(token);

        return Response.success("Password has been reset successfully", null);
    }

    private boolean isPasswordStrong(String password) {
        // Implement password strength checks
        // For example:
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }
}