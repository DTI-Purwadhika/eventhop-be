package com.riri.eventhop.feature2.users.auth;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.feature2.users.UserRepository;
import com.riri.eventhop.feature2.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("id");
        }
        throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected authentication type");
    }
    @Override
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }
        throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected authentication type");
    }
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
    @Override
    public User getUserByPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token).getUser();
    }
    @Override
    @Transactional
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }
    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);

        if (passToken == null) {
            return "invalidToken";
        }

        if (isTokenExpired(passToken)) {
            passwordResetTokenRepository.delete(passToken);
            return "expired";
        }

        return null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().isBefore(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
    @Override
    @Transactional
    public void invalidatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken != null) {
            passwordResetTokenRepository.delete(resetToken);
        }
    }
}
