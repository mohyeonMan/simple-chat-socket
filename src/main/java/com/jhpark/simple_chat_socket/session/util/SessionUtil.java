package com.jhpark.simple_chat_socket.session.util;

import java.security.Principal;

import org.springframework.stereotype.Component;

import com.jhpark.simple_chat_socket.session.dto.SessionPrincipal;

@Component
public class SessionUtil {
    
    public static Long extractUserIdBySessionName(final String sessionName){
        return Long.parseLong(sessionName.split(":")[0]);
    }

    public static String extractSessionIdBySessionName(final String sessionName){
        return sessionName.split(":")[1];
    }

    
    public static Long extractUserIdFromPrincipal(final Principal principalObj) {
        
        if(principalObj instanceof SessionPrincipal){
            return ((SessionPrincipal) principalObj).getUserId();
        }
        
        throw new RuntimeException("Failed to extract user id from principal");
    }

    public static SessionPrincipal castSessionPrincipalFromPrincipal(final Principal principal) {
        if(principal instanceof SessionPrincipal){
            return (SessionPrincipal) principal;
        }
        
        throw new RuntimeException("Failed to casting session principal from principal");
    }
    
    public static String getSessionName(final Long userId, final String sessionId) {
        return userId + ":" + sessionId;
    }


}
