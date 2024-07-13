package com.riri.eventhop.feature2.users.dto;

import com.riri.eventhop.feature2.users.entity.User;
import lombok.Data;

import java.time.Instant;

@Data
public class ReferredUser {
    private String name;
    private String email;
    private Instant registrationDate;
    public static ReferredUser fromUser(User user) {
        ReferredUser referredUser = new ReferredUser();
        referredUser.setName(user.getName());
        referredUser.setEmail(user.getEmail());
        referredUser.setRegistrationDate(user.getCreatedAt());
        return referredUser;
    }
}
