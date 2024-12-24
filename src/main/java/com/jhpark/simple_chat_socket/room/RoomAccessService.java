package com.jhpark.simple_chat_socket.room;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jhpark.simple_chat_socket.redis.service.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomAccessService {

    private final RedisService redisService;
    
    
}
