package com.riri.eventhop.feature2.users.dto;

import lombok.Data;

@Data
public class OAuthLoginRequest {
    private String name;
    private String email;
    private String avatarUrl;
    private String provider;
}
