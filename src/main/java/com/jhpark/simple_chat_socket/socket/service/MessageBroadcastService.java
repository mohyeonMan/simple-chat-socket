package com.jhpark.simple_chat_socket.socket.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.common.util.ObjectMapperUtil;
import com.jhpark.simple_chat_socket.session.service.SessionSyncronizationService;
import com.jhpark.simple_chat_socket.session.util.SessionUtil;
import com.jhpark.simple_chat_socket.socket.dto.broadcast.BroadcastMessage;
import com.jhpark.simple_chat_socket.socket.dto.broadcast.UserSessionInfo;
import com.jhpark.simple_chat_socket.socket.util.DestinationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBroadcastService {
        private final SessionSyncronizationService sessionSyncronizationService;
        private final SimpMessagingTemplate messagingTemplate;
        private final ObjectMapperUtil objectMapperUtil;

        public void broadcastMessage(
                        final Long senderId,
                        final Set<UserSessionInfo> userSessionInfos,
                        final String roomId,
                        final String message) {

                final BroadcastMessage payload = BroadcastMessage.builder()
                                .senderId(senderId)
                                .message(message)
                                .roomId(roomId)
                                .build();

                userSessionInfos.stream().parallel().forEach(userSession -> {
                        final Long userId = userSession.getUserId();
                        userSession.getSessionIds().stream().forEach(sessionId -> {
                                final String sessionName = SessionUtil.getSessionName(userId, sessionId);

                                sessionSyncronizationService.synchronizeSessionOffline(sessionName, roomId);

                                log.info("Broadcasting : sessionName={}, destination={}, message={}",
                                sessionName, DestinationUtil.getBroadcastDestination(roomId), message);

                                messagingTemplate.convertAndSendToUser(
                                        sessionName,
                                        DestinationUtil.getBroadcastDestination(roomId),
                                        objectMapperUtil.writeValueAsString(payload));
                        });
                });

        }
}
