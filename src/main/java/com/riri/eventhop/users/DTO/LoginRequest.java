package com.riri.eventhop.users.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
