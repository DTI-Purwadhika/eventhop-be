package com.riri.eventhop.feature2.users;

import com.riri.eventhop.config.JwtTokenProvider;
import com.riri.eventhop.feature2.users.dto.JwtAuthenticationResponse;
import com.riri.eventhop.feature2.users.dto.LoginRequest;
import com.riri.eventhop.feature2.users.dto.OAuthLoginRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

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
}