package com.jhpark.simple_chat_socket.socket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jhpark.simple_chat_socket.security.service.AuthService;
import com.jhpark.simple_chat_socket.session.dto.SessionPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final AuthService authService;
    private static final String AUTH_PREFIX = "Bearer ";
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }
        
        final String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith(AUTH_PREFIX)) {
            final String token = authHeader.substring(AUTH_PREFIX.length());
            Authentication authentication = authService.checkAccessTokenAndSetAuthentication(token);
            accessor.setUser(
                SessionPrincipal.builder()
                .sessionId(accessor.getSessionId())
                .userId(Long.valueOf(authentication.getName()))
                .build()
            );
            log.info("Authentication = {}", SecurityContextHolder.getContext().getAuthentication().toString());
    
            return message;
        }else{
            throw new RuntimeException("Authorization is required");
        }

    }

}
