package com.jhpark.simple_chat_socket.session.dto;

import java.security.Principal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionPrincipal implements Principal {
    
        private final Long userId;
        private final String sessionId;
        private final String token;
    
        @Override
        public String getName() {
            return userId + ":" + sessionId;
        }
    
}