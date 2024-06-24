package com.riri.eventhop.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthTestController {

    @GetMapping("/test")
    public ResponseEntity<?> testAuth(@AuthenticationPrincipal Jwt jwt) {
        try {
            if (jwt == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No JWT token found");
            }

            String userId = jwt.getSubject();
            String email = jwt.getClaim("email");
            String name = jwt.getClaim("name");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Authentication successful");
            response.put("userId", userId);
            response.put("email", email);
            response.put("name", name);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: " + e.getMessage());
        }
    }
}