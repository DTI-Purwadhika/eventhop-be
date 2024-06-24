package com.riri.eventhop.users.dto;

import lombok.Data;
import com.riri.eventhop.users.entity.User;

@Data
public class ClerkUserDTO {
    private String clerkId;
    // We don't store these in our User entity, but we might need them for operations
    private String email;
    private String name;
    private String username;

    public User toEntity() {
        User user = new User();
        user.setClerkId(clerkId);
        // We don't set email, name, or username as they're not stored in our User entity
        return user;
    }
}