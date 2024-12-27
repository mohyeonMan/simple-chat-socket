package com.jhpark.simple_chat_socket.session.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.kafka.service.KafkaMessageService;
import com.jhpark.simple_chat_socket.session.util.SessionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionSyncronizationService {

    private final KafkaMessageService kafkaMessagePublisher;
    private final SessionRegistryService sessionRegistryService;

    @Async
    public void synchronizeSessionOffline(
            final String sessionName,
            final Long roomId) {

        if (!sessionRegistryService.isSessionSubscribedRoom(sessionName, roomId)) {

            kafkaMessagePublisher.synchronizeSessionOnline(
                    SessionUtil.extractUserIdBySessionName(sessionName),
                    SessionUtil.extractSessionIdBySessionName(sessionName), roomId);

            log.warn("SESSION SYNCRONIZE EVENT TO KAFKA: sessionName={}, roomId={}",
                    sessionName, roomId);
        }

    }

}
