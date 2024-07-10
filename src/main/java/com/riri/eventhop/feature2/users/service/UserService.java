package com.riri.eventhop.feature2.users.service;


import com.riri.eventhop.feature2.users.dto.RegisterRequest;
import com.riri.eventhop.feature2.users.entity.User;

public interface UserService {
    User registerUser(RegisterRequest request);
    Integer calculateAvailablePoints(User user);
    Integer getAvailableDiscount(User user);
    void redeemPoints(User user, int pointsToRedeem);
    User findByEmail(String email);
    User save(User user);
}
