package com.riri.eventhop.feature2.users.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
