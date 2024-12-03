package com.jhpark.simple_chat_socket.socket.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.common.util.ObjectMapperUtil;
import com.jhpark.simple_chat_socket.socket.dto.broadcast.BroadcastMessage;
import com.jhpark.simple_chat_socket.socket.util.DestinationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBroadcastService {

    private final SessionRegistryService sessionRegistryService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapperUtil objectMapperUtil;

    public void broadcastMessage(
            final Long senderId,
            final String roomId,
            final String message,
            final Set<Long> userIds) {

        final BroadcastMessage payload = BroadcastMessage.builder()
                .senderId(senderId)
                .message(message)
                .roomId(roomId)
                .build();

        userIds.stream().parallel().forEach(userId -> {

            if (!sessionRegistryService.isUserSubscribed(userId, roomId)) {
                log.warn("User {} is not online. Skipping message broadcast.", userId);
                return;
            }

            log.info("Broadcasting : userId={}, destination={}, message={}",
                    userId, DestinationUtil.getUserDestination(roomId),
                    objectMapperUtil.writeValueAsString(payload));

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(userId),
                    DestinationUtil.getUserDestination(roomId),
                    objectMapperUtil.writeValueAsString(payload));
        });

    }
}
