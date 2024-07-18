    package com.riri.eventhop.util;

    import com.riri.eventhop.feature2.users.auth.TokenBlacklistService;
    import io.jsonwebtoken.*;
    import io.jsonwebtoken.security.Keys;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Component;
    import com.riri.eventhop.feature2.users.entity.User;

    import javax.crypto.SecretKey;
    import java.util.Date;
    import java.util.stream.Collectors;

    @Component
    @Slf4j
    public class JwtTokenProvider {
        @Value("${jwt.expirationInMs}")
        private Long jwtExpirationInMs;

        @Value("${jwt.refreshExpirationInMs}")
        private Long refreshExpirationInMs;
//        private final RsaKeyConfigProperties rsaKeyConfigProperties;
        @Value("${JWT_SECRET}")
        private String jwtSecret;
        private TokenBlacklistService tokenBlacklistService;

        private SecretKey getSigningKey() {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }

//        public JwtTokenProvider(RsaKeyConfigProperties rsaKeyConfigProperties) {
//            this.rsaKeyConfigProperties = rsaKeyConfigProperties;
//        }

        public String generateToken(User user) {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

            return Jwts.builder()
                    .subject(user.getEmail()) // Use email as the subject
                    .claim("email", user.getEmail())
                    .claim("userId", user.getId())
                    .claim("name", user.getName())
                    .claim("referralCode", user.getReferralCode())
                    .claim("bio", user.getBio())
                    .claim("location", user.getLocation())
                    .claim("avatarUrl", user.getAvatarUrl())
                    .claim("user_role", user.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        }

        public String generateRefreshToken(User user) {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + refreshExpirationInMs);

            return Jwts.builder()
                    .subject(Long.toString(user.getId()))
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        }
        public boolean validateToken(String token) {
            try {
                if (tokenBlacklistService.isBlacklisted(token)) {
                    return false;
                }

                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token);

                return true;
            } catch (SecurityException | MalformedJwtException e) {
                log.error("Invalid JWT signature: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                log.error("JWT token is expired: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.error("JWT token is unsupported: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error("JWT claims string is empty: {}", e.getMessage());
            }

            return false;
        }
//        public boolean validateToken(String token) {
//            try {
//                if (tokenBlacklistService.isBlacklisted(token)) {
//                    System.out.println("Token is blacklisted, rejecting");
//                    return false;
//                }
//                Jwts.parser()
//                        .verifyWith(getSigningKey())
//                        .build()
//                        .parseSignedClaims(token);
//                return true;
//            } catch (JwtException | IllegalArgumentException e) {
//                return false;
//            }
//        }
    }