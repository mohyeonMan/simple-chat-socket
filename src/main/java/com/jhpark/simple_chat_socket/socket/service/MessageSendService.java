package com.jhpark.simple_chat_socket.socket.service;

import java.security.Principal;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.kafka.dto.ChatMessage;
import com.jhpark.simple_chat_socket.kafka.service.KafkaService;
import com.jhpark.simple_chat_socket.security.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSendService {

    private final SessionRegistryService sessionRegistryService;
    private final KafkaService kafkaService;

    public void sendMessage(
            final String roomId,
            final String message,
            final Principal principal
    ) {
        final Long senderId = SecurityUtil.extractUserIdFromPrincipal(principal);

        sessionRegistryService.validateUserSubscription(senderId, roomId);

        // roomId로 같은 채팅방 내의 사용자들 조회.
        final Set<Long> userIds = Set.of(1L, 2L, 3L, 4L, 5L);

        kafkaService.sendChatMessage(
            ChatMessage.builder()
                .senderId(senderId)
                .roomId(roomId)
                .userIds(userIds)
                .message(message)
                .build()
        );
    }

}
