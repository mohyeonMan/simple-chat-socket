package com.jhpark.simple_chat_socket.socket.dto.broadcast;

import java.util.Set;

import lombok.Data;

@Data
public class BroadcastRequest {
    private Long senderId;
    private String message;
    private String roomId;
    private Set<Long> userIds;
}