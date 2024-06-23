package com.riri.eventhop.users.controller;

import com.riri.eventhop.helpers.Claims;
import com.riri.eventhop.users.DTO.RegisterRequest;
import com.riri.eventhop.users.service.UserService;
import com.riri.eventhop.response.Response;

import jakarta.annotation.security.RolesAllowed;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return Response.success("User registered successfully", userService.register(registerRequest));
    }

    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/profile")
    public ResponseEntity<?> profile() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        log.info("Claims are: " + claims.toString());
        log.info("User profile requested for user: " + email);
        return Response.success("User profile", userService.findByUsernameOrEmail(email));
    }
}
