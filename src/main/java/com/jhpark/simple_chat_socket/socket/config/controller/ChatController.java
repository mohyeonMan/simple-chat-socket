package com.jhpark.simple_chat_socket.socket.config.controller;

// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    
    @MessageMapping("/send")
    public void sendMessage(@Payload String message) {
        // Kafka로 메시지 전송
        log.info("SEND MESSAGE TO KAFKA: " + message);
        kafkaTemplate.send("chat-topic", message);
    }
}