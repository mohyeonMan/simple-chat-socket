package com.jhpark.simple_chat_socket.kafka.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMetadata {
    private Long userId;
    private String sessionId;
    private String serverIp;
}
