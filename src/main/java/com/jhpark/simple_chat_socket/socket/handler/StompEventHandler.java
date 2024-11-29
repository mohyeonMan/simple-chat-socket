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
import com.jhpark.simple_chat_socket.security.util.SecurityUtil;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventHandler {

    private final String serverIp;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapperUtil objectMapperUtil;

    private static final String SESSION_ONLINE_TOPIC = "session-online";
    private static final String SESSION_OFFLINE_TOPIC = "session-offline";

    public StompEventHandler(KafkaTemplate<String, String> kafkaTemplate, ObjectMapperUtil objectMapperUtil) throws Exception {
        this.kafkaTemplate = kafkaTemplate;
        this.serverIp = ServerIpUtil.getServerIp();
        this.objectMapperUtil = objectMapperUtil;
    }

    @Data
    @Builder
    public static class SessionMessage {
        private String sessionId;
        private String serverIp;
        private Long userId;
    }


    //구독 이벤트 카프카로 발행
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            final String sessionId = accessor.getSessionId();
            final String destination = accessor.getDestination(); // 구독한 토픽 정보
            final Long userId = SecurityUtil.extractUserIdFromPrincipal(accessor.getUser());
            //채팅방 구독 자격 검증 필요.

            final SessionMessage message = SessionMessage.builder()
                    .userId(userId)
                    .serverIp(serverIp)
                    .sessionId(sessionId)
                    .build();

            kafkaTemplate.send(SESSION_ONLINE_TOPIC, objectMapperUtil.writeValueAsString(message));
            log.info("Session Subscribe Event: sessionId={}, destination={}, userId={}, serverIp={}", sessionId, destination, userId, serverIp);

        }
    }

    //구독 해지 이벤트 카프카로 발행
    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        final Long userId = SecurityUtil.extractUserIdFromPrincipal(accessor.getUser());

        final SessionMessage message = SessionMessage.builder()
                .userId(userId)
                .build();

        kafkaTemplate.send(SESSION_OFFLINE_TOPIC, objectMapperUtil.writeValueAsString(message));
        log.info("Session Unsubscribe Event:  userId={}", userId);
    }

    //세션 연결 해제 이벤트 카프카로 발행
    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        final Long userId = SecurityUtil.extractUserIdFromPrincipal(accessor.getUser());

        final SessionMessage message = SessionMessage.builder()
                .userId(userId)
                .build();

        kafkaTemplate.send(SESSION_OFFLINE_TOPIC, objectMapperUtil.writeValueAsString(message));
        log.info("Session Disconnected Event:  userId={}", userId);
    }

}
