package com.riri.eventhop.users.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
public class AuthRedisRepository {

    private static final String STRING_KEY_PREFIX = "eventhop:jwt:strings:";
    private final Jedis jedis;

    @Autowired
    public AuthRedisRepository(Jedis jedis) {
        this.jedis = jedis;
    }

    public void saveJwtKey(String email, String jwtKey) {
        jedis.setex(STRING_KEY_PREFIX + email, 3600, jwtKey); // 3600 seconds = 1 hour
    }

    public String getJwtKey(String email) {
        return jedis.get(STRING_KEY_PREFIX + email);
    }

    public void deleteJwtKey(String email) {
        jedis.del(STRING_KEY_PREFIX + email);
    }


}
