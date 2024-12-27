package com.jhpark.simple_chat_socket.socket.dto.broadcast;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BroadcastMessage {
    private Long senderId;
    private String message;
    private Long roomId;
}