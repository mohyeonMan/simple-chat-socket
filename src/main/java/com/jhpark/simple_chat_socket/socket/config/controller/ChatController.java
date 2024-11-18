package com.jhpark.simple_chat_socket.socket.config.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    // 클라이언트가 "/app/chat"으로 메시지를 보낼 때 처리
    @MessageMapping("/chat")
    public void sendMessage(String message) {
        log.info("클라이언트로부터 받은 메시지: " + message);
        // Redis에 메시지 발행
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}