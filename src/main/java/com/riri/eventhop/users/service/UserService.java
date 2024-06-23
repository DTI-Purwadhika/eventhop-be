package com.riri.eventhop.users.service;

import com.riri.eventhop.users.DTO.RegisterRequest;
import com.riri.eventhop.users.entity.User;

import java.util.List;

public interface UserService {
    User register(RegisterRequest registerRequest);
    User findByUsernameOrEmail(String usernameOrEmail);
    User findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
    User profile();

}
