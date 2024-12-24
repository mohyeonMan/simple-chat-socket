package com.jhpark.simple_chat_socket.redis.service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public Set<String> get(final String key) {
        log.debug("REDIS GET : key={}", key);
        final Set<String> members = redisTemplate.opsForSet().members(key);
        if (members == null || members.isEmpty()) {
            return Set.of();
        }
        return members;
    }

}
