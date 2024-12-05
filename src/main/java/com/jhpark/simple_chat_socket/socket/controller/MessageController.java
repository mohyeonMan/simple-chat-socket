package com.jhpark.simple_chat_socket.socket.controller;

import java.security.Principal;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.stereotype.Controller;

import com.jhpark.simple_chat_socket.socket.dto.broadcast.BroadcastRequest;
import com.jhpark.simple_chat_socket.socket.service.MessageBroadcastService;
import com.jhpark.simple_chat_socket.socket.service.MessageSendService;
import com.jhpark.simple_chat_socket.socket.service.SessionRegistryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SessionRegistryService sessionRegistryService;
    private final MessageBroadcastService messageBroadcastService;
    private final MessageSendService messageSendService;

    @MessageMapping("/send/{roomId}")
    public void sendMessage(
            @DestinationVariable("roomId") String roomId,
            @Payload String message,
            Principal principal
    ) {
        messageSendService.sendMessage(roomId, message, principal);
    }

    @GetMapping("check-users")
    public ResponseEntity<Set<SimpUser>> getMethodName() {
        return ResponseEntity.ok().body(sessionRegistryService.getUsers());
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