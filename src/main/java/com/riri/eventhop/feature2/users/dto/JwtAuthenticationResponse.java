package com.riri.eventhop.feature2.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private Long id;
    private String email;
    private String name;
    private String roles;
    private String avatarUrl;
    private String token;
    private String refreshToken;
}
