package com.riri.eventhop.users.dto;

import com.riri.eventhop.users.entity.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String clerkId;
    private String referralCode;
    private Long points;
    private String organizerName;
    // Add any other fields from the User entity you want to expose

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setClerkId(user.getClerkId());
        dto.setReferralCode(user.getReferralCode());
        dto.setPoints(user.getPoints());
        dto.setOrganizerName(user.getOrganizerName());
        // Set any other fields from the User entity
        return dto;
    }
}