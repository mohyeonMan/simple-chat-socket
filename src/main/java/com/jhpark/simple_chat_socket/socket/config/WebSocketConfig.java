package com.jhpark.simple_chat_socket.socket.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.jhpark.simple_chat_socket.socket.interceptor.JwtChannelInterceptor;
import com.jhpark.simple_chat_socket.socket.interceptor.SubscriptionInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;
    private final SubscriptionInterceptor subscriptionInterceptor;

    private static final String TOPIC_PREFIX = "/join";
    public static final String QUEUE_PREFIX = "/private";
    private static final String DESTINATION_PREFIX =  "/message";
    private static final String END_POINT = "/ws-chat";
    public static final String USER_SUBSCRIBE_PREFIX = "/user"+ QUEUE_PREFIX +"/";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(TOPIC_PREFIX, QUEUE_PREFIX);
        config.setApplicationDestinationPrefixes(DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint(END_POINT)
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration
            .interceptors(jwtChannelInterceptor)
            .interceptors(subscriptionInterceptor)
        ;
    }
}