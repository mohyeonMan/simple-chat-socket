package com.jhpark.simple_chat_socket.socket.dto.broadcast;

import java.util.Set;

import lombok.Data;

@Data
public class BroadcastRequest {
    private Long senderId;
    private Set<UserSessionInfo> userSessionInfos;
    private String roomId;
    private String message;
}