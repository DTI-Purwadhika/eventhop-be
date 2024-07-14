package com.riri.eventhop.feature2.users.dto;

import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.feature2.users.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String email;
    private String name;
    private Set<UserRole> roles;
    private String referralCode;
    private String bio;
    private String location;
    private String avatarUrl;

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
                user.getAvatarUrl()
        );
    }
}