package com.jhpark.simple_chat_socket.kafka.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SessionOnlineMessage {

    private Long userId;
    private String sessionId;
    private Long roomId;
    private String serverIp;

}
