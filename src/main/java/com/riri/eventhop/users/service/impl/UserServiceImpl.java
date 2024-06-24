package com.riri.eventhop.users.service.impl;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.users.dto.UserProfileDTO;
import com.riri.eventhop.users.entity.User;
import com.riri.eventhop.users.repository.UserRepository;
import com.riri.eventhop.users.service.UserService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User findOrCreateUser(String clerkId) {
        return userRepository.findByClerkId(clerkId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setClerkId(clerkId);
                    return userRepository.save(newUser);
                });
    }

    @Override
    public UserProfileDTO getUserProfile(String clerkId) {
        User user = findOrCreateUser(clerkId);
        return mapUserToProfileDTO(user);
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(String clerkId, UserProfileDTO profileDTO) {
        User user = findOrCreateUser(clerkId);
        updateUserFromProfileDTO(user, profileDTO);
        User updatedUser = userRepository.save(user);
        return mapUserToProfileDTO(updatedUser);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User addRole(String clerkId, String role) {
        User user = findOrCreateUser(clerkId);
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User removeRole(String clerkId, String role) {
        User user = findOrCreateUser(clerkId);
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateReferralCode(String clerkId, String referralCode) {
        User user = findOrCreateUser(clerkId);
        user.setReferralCode(referralCode);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updatePoints(String clerkId, Long points) {
        User user = findOrCreateUser(clerkId);
        user.setPoints(points);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateOrganizerName(String clerkId, String organizerName) {
        User user = findOrCreateUser(clerkId);
        user.setOrganizerName(organizerName);
        return userRepository.save(user);
    }

    private UserProfileDTO mapUserToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setImageUrl(user.getImageUrl());
        dto.setOrganizerName(user.getOrganizerName());
        dto.setReferralCode(user.getReferralCode());
        dto.setPoints(user.getPoints());
        dto.setRoles(user.getRoles());
        return dto;
    }

    private void updateUserFromProfileDTO(User user, UserProfileDTO dto) {
        // Only update fields that are not managed by Clerk
        user.setOrganizerName(dto.getOrganizerName());
        user.setReferralCode(dto.getReferralCode());
        user.setPoints(dto.getPoints());
        // Roles should be managed separately for security reasons
    }
}