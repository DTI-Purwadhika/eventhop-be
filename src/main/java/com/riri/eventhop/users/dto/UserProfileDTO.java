package com.riri.eventhop.users.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserProfileDTO {
    private String name;
    private String email;
    private String imageUrl;
    private String organizerName;
    private String referralCode;
    private Long points;
    private Set<String> roles;


    // Add any other profile fields you want to expose or allow updating
}