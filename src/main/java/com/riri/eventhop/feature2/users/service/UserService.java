package com.riri.eventhop.feature2.users.service;


import com.riri.eventhop.feature2.users.dto.RegisterRequest;
import com.riri.eventhop.feature2.users.entity.Discount;
import com.riri.eventhop.feature2.users.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(RegisterRequest request);
    Integer calculateAvailablePoints(User user);
    Discount getAvailableReferralDiscount(User user);
    void useReferralDiscount(User user);
    void redeemPoints(User user, int pointsToRedeem);
    List<User> findReferredUsers(User referrer);

}
