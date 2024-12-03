package com.jhpark.simple_chat_socket.kafka.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.common.util.ObjectMapperUtil;
import com.jhpark.simple_chat_socket.kafka.dto.ChatMessage;
import com.jhpark.simple_chat_socket.kafka.dto.SessionOfflineMessage;
import com.jhpark.simple_chat_socket.kafka.dto.SessionOnlineMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private static final String MESSAGE_TOPIC = "chat-message";
    private static final String SESSION_ONLINE_TOPIC = "session-online";
    private static final String SESSION_OFFLINE_TOPIC = "session-offline";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapperUtil objectMapperUtil;

    public void sendChatMessage(final ChatMessage message) {
        log.info("SEND MESSAGE TO KAFKA : senderId={}, userIds={}, roomId={}, message={}",
                message.getSenderId(), message.getUserIds(), message.getRoomId(), message.getMessage());
        kafkaTemplate.send(MESSAGE_TOPIC, objectMapperUtil.writeValueAsString(message));
    }

    public void sendSessionOnlineMessage(final SessionOnlineMessage message) {
        log.info("SUBSCRIBE EVENT TO KAFKA: userId={}, sessionId={}, roomId={}, serverIp={}",
                message.getUserId(), message.getSessionId(), message.getRoomId(), message.getServerIp());
        kafkaTemplate.send(SESSION_ONLINE_TOPIC, objectMapperUtil.writeValueAsString(message));
    }

    public void sendSessionOfflineMessage(final SessionOfflineMessage message) {
        log.info("UNSUBSCRIBE EVENT TO KAFKA: userId={}, sessionId={}",
                message.getUserId(), message.getSessionId());
        kafkaTemplate.send(SESSION_OFFLINE_TOPIC, objectMapperUtil.writeValueAsString(message));
    }

}
