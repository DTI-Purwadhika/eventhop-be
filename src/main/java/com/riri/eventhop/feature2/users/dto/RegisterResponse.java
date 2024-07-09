package com.riri.eventhop.feature2.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String name;
    private String email;
    private String referralCode;
}
