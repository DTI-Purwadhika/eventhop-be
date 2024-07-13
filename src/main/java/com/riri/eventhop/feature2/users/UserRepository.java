package com.riri.eventhop.feature2.users;

import com.riri.eventhop.feature2.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByReferralCode(String referralCode);
    List<User> findByReferrer(User referrer);

}
