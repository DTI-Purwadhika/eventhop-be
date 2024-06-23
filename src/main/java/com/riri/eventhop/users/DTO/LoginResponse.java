package com.riri.eventhop.users.DTO;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private String token;
}