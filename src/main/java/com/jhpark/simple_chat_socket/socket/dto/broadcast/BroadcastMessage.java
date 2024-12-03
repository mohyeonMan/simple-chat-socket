package com.jhpark.simple_chat_socket.socket.dto.broadcast;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BroadcastMessage {
    private Long senderId; // 메시지 발신자
    private String message; // 메시지 내용
    private String roomId;
}