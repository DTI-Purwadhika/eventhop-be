package com.riri.eventhop.feature2.users;

//import com.riri.eventhop.feature2.referrals.ReferralService;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.riri.eventhop.feature2.users.dto.RegisterRequest;
import com.riri.eventhop.feature2.users.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;
//    private final ReferralService referralService;
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<UserInfoDTO> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        log.info("Received request for user info. JWT: {}", jwt);
        if (jwt == null) {
            log.warn("JWT is null in getUserInfo method");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwt.getClaimAsString("sub");  // Assuming email is stored in the 'sub' claim
        User user = userService.findByEmail(email);
        if (user == null) {
            log.warn("User not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserInfoDTO userInfoDTO = UserInfoDTO.fromUser(user);
        return ResponseEntity.ok(userInfoDTO);
    }



}
