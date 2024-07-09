package com.riri.eventhop.feature2.users;


public interface UserService {

    User findByEmail(String email);
    User save(User user);
}
