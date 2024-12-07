package com.jhpark.simple_chat_socket.socket.handler;


import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.jhpark.simple_chat_socket.kafka.service.KafkaMessageService;
import com.jhpark.simple_chat_socket.session.util.SessionUtil;
import com.jhpark.simple_chat_socket.socket.util.DestinationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventHandler {

    private final KafkaMessageService kafkaMessagePublisher;

    //구독 이벤트 카프카로 발행
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {

        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (!StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return;
        }

        final Long userId = SessionUtil.extractUserIdFromPrincipal(accessor.getUser());

        DestinationUtil.validateDestination(accessor.getDestination());

        final String roomId = DestinationUtil.extractRoomIdByDestination(accessor.getDestination());
        final String sessionId = accessor.getSessionId();

        kafkaMessagePublisher.synchronizeSessionOnline(userId, sessionId, roomId);

    }

    //구독 해지 이벤트 카프카로 발행
    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        final Long userId = SessionUtil.extractUserIdFromPrincipal(accessor.getUser());
        final String sessionId = accessor.getSessionId();

        kafkaMessagePublisher.synchronizeSessionOffline(userId, sessionId);

    }

    //세션 연결 해제 이벤트 카프카로 발행
    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        final Long userId = SessionUtil.extractUserIdFromPrincipal(accessor.getUser());
        final String sessionId = accessor.getSessionId();

        kafkaMessagePublisher.synchronizeSessionOffline(userId, sessionId);
    }

}
