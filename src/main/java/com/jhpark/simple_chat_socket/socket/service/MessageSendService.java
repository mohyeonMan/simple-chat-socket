package com.jhpark.simple_chat_socket.socket.service;

import java.util.Set;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.kafka.service.KafkaMessageService;
import com.jhpark.simple_chat_socket.room.RoomService;
import com.jhpark.simple_chat_socket.session.dto.SessionPrincipal;
import com.jhpark.simple_chat_socket.session.service.SessionRegistryService;
import com.jhpark.simple_chat_socket.session.util.SessionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSendService {

    private final SessionRegistryService sessionRegistryService;
    private final KafkaMessageService kafkaMessagePublisher;
    private final RoomService roomService;

    public void sendMessage(
            final String roomId,
            final String message,
            final SimpMessageHeaderAccessor accessor
    ) {

        final SessionPrincipal sessionPrincipal = SessionUtil.castSessionPrincipalFromPrincipal(accessor.getUser());    

        // 세션 구독 검증
        sessionRegistryService.validateSessionSubscription(sessionPrincipal.getName(), roomId);

        // roomId로 같은 채팅방 내의 사용자들 조회.
        final Set<Long> userIds = roomService.getRoomUserIds(roomId,accessor.getUser());

        kafkaMessagePublisher.sendChatMessage(
            sessionPrincipal.getName(),
            userIds,
            roomId,
            message);
    }

}
