package com.riri.eventhop.feature2.users.dto;

import com.riri.eventhop.feature2.users.User;
import com.riri.eventhop.feature2.users.UserRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class UserInfoDTO {
    private final Long id;
    private final String email;
    private final String name;
    private final Set<UserRole> roles;
    private final String referralCode;
    private final String bio;
    private final String location;
    private final String avatarUrl;
    private final String website;

    public static UserInfoDTO fromUser(User user) {
        if (user == null) {
            return null; // or throw a custom exception
        }
        return new UserInfoDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRoles(),
                user.getReferralCode(),
                user.getBio(),
                user.getLocation(),
                user.getAvatarUrl(),
                user.getWebsite()
        );
    }
}