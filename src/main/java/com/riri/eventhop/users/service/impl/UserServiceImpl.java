package com.riri.eventhop.users.service.impl;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.users.DTO.RegisterRequest;
import com.riri.eventhop.users.entity.User;
import com.riri.eventhop.users.repository.UserRepository;
import com.riri.eventhop.users.service.UserService;
import lombok.extern.java.Log;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail())) {
            throw new ApplicationException("Username or email already exists");
        }

        User newUser = registerRequest.toEntity();
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return userRepository.save(newUser);
    }

    @Override
    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ApplicationException("User not found with username or email: " + usernameOrEmail));
    }


    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("User not found with id: " + id));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ApplicationException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User profile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsernameOrEmail(username);
    }

//    @Transactional
//    public void updateAvatar(Long userId, String imageUrl) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        user.setAvatarUrl(imageUrl); // Assuming User entity has a field for avatar URL
//
//        userRepository.save(user);
//    }
}
