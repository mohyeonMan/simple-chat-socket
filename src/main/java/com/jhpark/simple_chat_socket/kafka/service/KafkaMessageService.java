package com.jhpark.simple_chat_socket.kafka.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.common.util.ObjectMapperUtil;
import com.jhpark.simple_chat_socket.common.util.ServerIpUtil;
import com.jhpark.simple_chat_socket.kafka.dto.ChatMessage;
import com.jhpark.simple_chat_socket.kafka.dto.SessionOfflineMessage;
import com.jhpark.simple_chat_socket.kafka.dto.SessionOnlineMessage;
import com.jhpark.simple_chat_socket.kafka.dto.UserMetadata;
import com.jhpark.simple_chat_socket.session.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaMessageService {

    private static final String MESSAGE_TOPIC = "chat-message";
    private static final String SESSION_ONLINE_TOPIC = "session-online";
    private static final String SESSION_OFFLINE_TOPIC = "session-offline";

    private final String serverIp;
    private final ObjectMapperUtil objectMapperUtil;
    private final KafkaPublisher kafkaPublisher;

    public KafkaMessageService(
            final ServerIpUtil serverIpUtil,
            final ObjectMapperUtil objectMapperUtil,
            final KafkaPublisher kafkaPublisher) {
        this.serverIp = serverIpUtil.getServerIp();
        this.objectMapperUtil = objectMapperUtil;
        this.kafkaPublisher = kafkaPublisher;
    }

    public void synchronizeSessionOnline(
            final Long userId,
            final String sessionId,
            final String roomId) {

        log.info("SUBSCRIBE EVENT TO KAFKA: userId={}, sessionId={}, roomId={}, serverIp={}",
                userId, sessionId, roomId, serverIp);

        kafkaPublisher.publish(
                SESSION_ONLINE_TOPIC,
                objectMapperUtil.writeValueAsString(SessionOnlineMessage.builder()
                        .userId(userId)
                        .serverIp(serverIp)
                        .sessionId(sessionId)
                        .roomId(roomId)
                        .build()));
    }

    public void synchronizeSessionOffline(
            final Long userId,
            final String sessionId) {
        log.info("UNSUBSCRIBE EVENT TO KAFKA: userId={}, sessionId={} serverIp={}",
                userId, sessionId, serverIp);

        kafkaPublisher.publish(
                SESSION_OFFLINE_TOPIC,
                objectMapperUtil.writeValueAsString(SessionOfflineMessage.builder()
                        .userId(userId)
                        .sessionId(sessionId)
                        .serverIp(serverIp)
                        .build()));
    }

    public void sendChatMessage(
            final String senderSessionName,
            final Set<Long> userIds,
            final String roomId,
            final String message) {

        log.info("SEND MESSAGE TO KAFKA : senderSessionName={}, userIds={}, roomId={}, message={}",
                senderSessionName, userIds, roomId, message);

        final UserMetadata userMetadata = UserMetadata.builder()
                .userId(SessionUtil.extractUserIdBySessionName(senderSessionName))
                .sessionId(SessionUtil.extractSessionIdBySessionName( senderSessionName))
                .serverIp(serverIp)
                .build();

        kafkaPublisher.publish(
                MESSAGE_TOPIC,
                objectMapperUtil.writeValueAsString(
                        ChatMessage.builder()
                                .senderMetadata(userMetadata)
                                .roomId(roomId)
                                .userIds(userIds)
                                .message(message)
                                .build()));
    }

}
