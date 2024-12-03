package com.jhpark.simple_chat_socket.security.util;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {

            return ((Long) authentication.getPrincipal());
        }
        return null;
    }

    public static Long extractUserIdFromPrincipal(final Principal principalObj) {

        if(principalObj instanceof Authentication) {
            Authentication authentication = (Authentication) principalObj;
            return Long.valueOf(authentication.getPrincipal().toString());
        }
        return null;
    }

    
    
}
