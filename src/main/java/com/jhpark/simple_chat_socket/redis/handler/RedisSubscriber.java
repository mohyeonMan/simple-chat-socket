// package com.jhpark.simple_chat_socket.redis.handler;

// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class RedisSubscriber {

//     private final SimpMessagingTemplate messagingTemplate;

//     public void handleMessage(String message) {
//         log.info("Redis에서 받은 메시지: " + message);

//         // STOMP를 통해 메시지를 브로드캐스트 (예: "/topic/chat"으로)
//         messagingTemplate.convertAndSend("/topic/chat", message);
//     }
// }