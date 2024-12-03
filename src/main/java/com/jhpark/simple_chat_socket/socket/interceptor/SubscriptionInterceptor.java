package com.jhpark.simple_chat_socket.socket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.jhpark.simple_chat_socket.room.RoomAccessService;
import com.jhpark.simple_chat_socket.security.util.SecurityUtil;
import com.jhpark.simple_chat_socket.socket.util.DestinationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionInterceptor implements ChannelInterceptor{

    private final RoomAccessService roomAccessService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            log.info("accessor.getDestination() = {}",accessor.getDestination());
            DestinationUtil.validateDestination(accessor.getDestination());
            log.info("validation success");

            final String roomId = DestinationUtil.extractRoomIdByDestination(accessor.getDestination());
            final Long userId = SecurityUtil.extractUserIdFromPrincipal(accessor.getUser());

            // 구독 검증
            if (!roomAccessService.hasAccessToRoom(userId, roomId)) {
                throw new RuntimeException("User does not have access to room: " + roomId);
            }
        }

        return message;
    }
}
