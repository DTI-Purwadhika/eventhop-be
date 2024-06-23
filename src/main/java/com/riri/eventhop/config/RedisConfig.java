package com.riri.eventhop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;

import java.time.Duration;


@Configuration
public class RedisConfig {
    @Bean
    public Jedis jedis() {
        String redisHost = "relative-heron-35748.upstash.io";
        int redisPort = 6379;
        String redisPassword = "AYukAAIncDEwZTk3ZWQzMWRjMDE0MWNjYmRlYTYxNTgzOGUzZDQyZnAxMzU3NDg";

        Jedis jedis = new Jedis(redisHost, redisPort, true); // SSL enabled
        jedis.auth(redisPassword);

        return jedis;
    }
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<?, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}