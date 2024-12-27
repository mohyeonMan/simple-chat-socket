package com.jhpark.simple_chat_socket.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {

            return ((Long) authentication.getPrincipal());
        }
        
        throw new RuntimeException("USER NOT AUTHORIZED.");
    }

    
}
