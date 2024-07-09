package com.riri.eventhop.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.riri.eventhop.feature2.users.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    @Value("${jwt.expirationInMs}")
    private Long jwtExpirationInMs;

    @Value("${jwt.refreshExpirationInMs}")
    private Long refreshExpirationInMs;
    private final RsaKeyConfigProperties rsaKeyConfigProperties;

    public JwtTokenProvider(RsaKeyConfigProperties rsaKeyConfigProperties) {
        this.rsaKeyConfigProperties = rsaKeyConfigProperties;
    }

//    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
//    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(user.getEmail()) // Use email as the subject
//                .claim("userId", user.getId())
                .claim("email", user.getEmail())
//                .claim("name", user.getName())
                .claim("user_role", user.getRoles().stream().map(Enum::name).collect(Collectors.toList()))                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(rsaKeyConfigProperties.privateKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationInMs);

        return Jwts.builder()
                .subject(Long.toString(user.getId()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(rsaKeyConfigProperties.privateKey())
                .compact();
    }

    // Add methods for token validation if needed
}