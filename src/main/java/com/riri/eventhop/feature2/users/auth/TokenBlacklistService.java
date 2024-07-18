package com.riri.eventhop.feature2.users.auth;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {
    private final Set<String> blacklist = new HashSet<>();

    public void blacklistToken(String token) {
        blacklist.add(token);
        System.out.println("Token blacklisted: " + token);
    }

    public boolean isBlacklisted(String token) {
        boolean result = blacklist.contains(token);
        System.out.println("Token " + token + " is blacklisted: " + result); // Temporary debug log
        return result;
    }
}