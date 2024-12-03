package com.jhpark.simple_chat_socket.kafka.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SessionOfflineMessage {

    private Long userId;
    private String sessionId;

}
