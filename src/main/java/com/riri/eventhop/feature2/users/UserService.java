package com.riri.eventhop.feature2.users;


import com.riri.eventhop.feature2.users.dto.RegisterRequest;

public interface UserService {
    User registerUser(RegisterRequest request);
    User findByEmail(String email);
    User save(User user);
}
