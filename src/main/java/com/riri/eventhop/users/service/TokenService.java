package com.riri.eventhop.users.service;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.users.entity.User;
import com.riri.eventhop.users.repository.AuthRedisRepository;
import com.riri.eventhop.users.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@Log
public class TokenService {

    private final JwtEncoder encoder;
    private final AuthRedisRepository authRedisRepository;
    private final UserRepository userRepository;

    public TokenService(JwtEncoder encoder, AuthRedisRepository authRedisRepository, UserRepository userRepository) {
        this.encoder = encoder;
        this.authRedisRepository = authRedisRepository;
        this.userRepository = userRepository;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var existingKey = authRedisRepository.getJwtKey(authentication.getName());
        if (existingKey != null) {
            log.info("Token already exists for user: " + authentication.getName());
            return existingKey;
        }

        User user = userRepository.findByUsernameOrEmail(authentication.getName(), authentication.getName())
                .orElseThrow(() -> new ApplicationException("User not found"));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("userId", user.getId())
                .build();
        String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        authRedisRepository.saveJwtKey(authentication.getName(), token);
        return token;
    }
}
