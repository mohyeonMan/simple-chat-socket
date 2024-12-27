package com.jhpark.simple_chat_socket.socket.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.jhpark.simple_chat_socket.socket.dto.broadcast.BroadcastRequest;
import com.jhpark.simple_chat_socket.socket.service.MessageBroadcastService;
import com.jhpark.simple_chat_socket.socket.service.MessageSendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageBroadcastService messageBroadcastService;
    private final MessageSendService messageSendService;

    @MessageMapping("/send/{roomId}")
    public void sendMessage(
            @DestinationVariable("roomId") Long roomId,
            @Payload String message,
            SimpMessageHeaderAccessor accessor
    ) {
        messageSendService.sendMessage(roomId, message, accessor);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastMessage(@RequestBody BroadcastRequest broadcastRequest) {

        messageBroadcastService.broadcastMessage(
                broadcastRequest.getSenderId(),
                broadcastRequest.getUserSessionInfos(),
                broadcastRequest.getRoomId(),
                broadcastRequest.getMessage());

        return ResponseEntity.ok().build();
    }

}