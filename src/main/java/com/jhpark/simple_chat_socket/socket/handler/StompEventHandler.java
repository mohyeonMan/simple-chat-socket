package com.jhpark.simple_chat_socket.socket.handler;


import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.jhpark.simple_chat_socket.common.util.ObjectMapperUtil;
import com.jhpark.simple_chat_socket.common.util.ServerIpUtil;
import com.jhpark.simple_chat_socket.kafka.dto.SessionOfflineMessage;
import com.jhpark.simple_chat_socket.kafka.dto.SessionOnlineMessage;
import com.jhpark.simple_chat_socket.kafka.service.KafkaService;
import com.jhpark.simple_chat_socket.security.util.SecurityUtil;
import com.jhpark.simple_chat_socket.socket.util.DestinationUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompEventHandler {

    private final String serverIp;
    private final KafkaService kafkaService;

    public StompEventHandler(
        KafkaService kafkaService,
        ObjectMapperUtil objectMapperUtil,
        ServerIpUtil serverIpUtil
    ) {
        this.serverIp = serverIpUtil.getServerIp();
        this.kafkaService = kafkaService;
    }

    //구독 이벤트 카프카로 발행
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {

        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (!StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return;
        }

        final Long userId = SecurityUtil.extractUserIdFromPrincipal(accessor.getUser());

        DestinationUtil.validateDestination(accessor.getDestination());

        final String roomId = DestinationUtil.extractRoomIdByDestination(accessor.getDestination());
        final String sessionId = accessor.getSessionId();

        kafkaService.sendSessionOnlineMessage(
            SessionOnlineMessage.builder()
                .userId(userId)
                .serverIp(serverIp)
                .sessionId(sessionId)
                .roomId(roomId)
                .build()
        );

    }

    //구독 해지 이벤트 카프카로 발행
    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        final Long userId = SecurityUtil.extractUserIdFromPrincipal(accessor.getUser());
        final String sessionId = accessor.getSessionId();

        kafkaService.sendSessionOfflineMessage(
            SessionOfflineMessage.builder()
                .userId(userId)
                .serverIp(serverIp)
                .sessionId(sessionId)
                .build()
        );

    }

    //세션 연결 해제 이벤트 카프카로 발행
    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        final Long userId = SecurityUtil.extractUserIdFromPrincipal(accessor.getUser());
        final String sessionId = accessor.getSessionId();

        kafkaService.sendSessionOfflineMessage(
            SessionOfflineMessage.builder()
                .userId(userId)
                .sessionId(sessionId)
                .build());

    }

}
