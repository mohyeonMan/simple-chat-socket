package com.jhpark.simple_chat_socket.socket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String TOPIC_PREFIX = "/topic";
    public static final String QUEUE_PREFIX = "/queue";
    public static final String DESTINATION_PREFIX =  "/app";
    public static final String END_POINT = "/ws-stomp";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 엔드포인트를 설정 (topic/queue로 브로드캐스트)
        config.enableSimpleBroker(TOPIC_PREFIX, QUEUE_PREFIX);
        
        // 클라이언트가 메시지를 보낼 때 사용할 prefix
        config.setApplicationDestinationPrefixes(DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트 정의 및 SockJS 지원
        registry.addEndpoint(END_POINT).setAllowedOriginPatterns("*")/* .withSockJS() */;
    }
}