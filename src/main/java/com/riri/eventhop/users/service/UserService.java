package com.riri.eventhop.users.service;

import com.riri.eventhop.users.entity.User;
import com.riri.eventhop.users.dto.UserProfileDTO;

import java.util.List;

public interface UserService {
    User findOrCreateUser(String clerkId);
    UserProfileDTO getUserProfile(String clerkId);
    UserProfileDTO updateUserProfile(String clerkId, UserProfileDTO profileDTO);
    List<User> findAll();
    User addRole(String clerkId, String role);
    User removeRole(String clerkId, String role);
    User updateReferralCode(String clerkId, String referralCode);
    User updatePoints(String clerkId, Long points);
    User updateOrganizerName(String clerkId, String organizerName);
}