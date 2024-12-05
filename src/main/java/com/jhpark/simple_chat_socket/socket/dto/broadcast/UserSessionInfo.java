package com.jhpark.simple_chat_socket.socket.dto.broadcast;

import java.util.Set;

import lombok.Data;

@Data
public class UserSessionInfo {

    private Long userId;
    private Set<String> sessionIds;
    
}