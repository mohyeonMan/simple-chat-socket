package com.jhpark.simple_chat_socket.socket.controller;

import java.security.Principal;

// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.jhpark.simple_chat_socket.common.util.ObjectMapperUtil;
import com.jhpark.simple_chat_socket.security.util.SecurityUtil;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapperUtil objectMapperUtil;

    @Data
    @Builder
    public static class ChatMessage {
        private Long userId;
        private String roomId;
        private String message;
    }

    @MessageMapping("/send/{roomId}")
    public void sendMessage(
        @DestinationVariable("roomId") String roomId,
        @Payload String message,
        Principal principal
    ) {

        final Long userId = SecurityUtil.extractUserIdFromPrincipal(principal);

        //roomId로 같은 채팅방 내의 사용자들 조회.

        final ChatMessage chatMessage = ChatMessage.builder()
                .userId(userId)
                .roomId(roomId)
                .message(message)
                .build();
                
        log.info("SEND MESSAGE TO KAFKA : user {}, roomId {}, message {} ", userId, roomId, message);
        kafkaTemplate.send("chat-message", objectMapperUtil.writeValueAsString(chatMessage));
    }
}