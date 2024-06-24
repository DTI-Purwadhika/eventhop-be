package com.riri.eventhop.users.controller;

import com.riri.eventhop.users.dto.UserProfileDTO;
import com.riri.eventhop.users.entity.User;
import com.riri.eventhop.users.service.UserService;
import com.riri.eventhop.response.Response;

import jakarta.annotation.security.RolesAllowed;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Log
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String clerkId = jwt.getSubject();
        UserProfileDTO profile = userService.getUserProfile(clerkId);
        return Response.success("User profile retrieved successfully", profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody UserProfileDTO profileDTO) {
        String clerkId = jwt.getSubject();
        UserProfileDTO updatedProfile = userService.updateUserProfile(clerkId, profileDTO);
        return Response.success("User profile updated successfully", updatedProfile);
    }
}
