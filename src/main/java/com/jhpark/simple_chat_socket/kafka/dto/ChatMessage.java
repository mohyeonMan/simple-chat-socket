package com.jhpark.simple_chat_socket.kafka.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {
    private Long senderId;
    private Set<Long> userIds;
    private String roomId;
    private String message;
}