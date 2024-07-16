package com.riri.eventhop.feature2.users.auth;

import com.riri.eventhop.feature2.users.entity.User;

public interface AuthService {
    String getCurrentUserId();
    String getCurrentUserEmail();
    User findByEmail(String email);
    User save(User user);

    void changeUserPassword(User user, String newPassword);

    User getUserByPasswordResetToken(String token);

    String validatePasswordResetToken(String token);

    void createPasswordResetTokenForUser(User user, String token);
    void invalidatePasswordResetToken(String token);
}
