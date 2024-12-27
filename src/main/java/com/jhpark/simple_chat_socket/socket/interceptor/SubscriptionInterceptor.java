package com.jhpark.simple_chat_socket.socket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.jhpark.simple_chat_socket.room.RoomService;
import com.jhpark.simple_chat_socket.session.dto.SessionPrincipal;
import com.jhpark.simple_chat_socket.session.service.SessionRegistryService;
import com.jhpark.simple_chat_socket.socket.util.DestinationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionInterceptor implements ChannelInterceptor{

    private final RoomService roomService;
    private final SessionRegistryService sessionRegistryService;


    /**
     * 구독요청이 들어올 시 처리순서
     * 1. 구독 경로를 검증한다.
     * 2. 현재 해당세션의 다른 구독이 없는지 검증한다. (세션 당 하나의 구독 유도)
     * 3. 사용자가 유저가 구독 권한이 있는지 검증한다.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            
            DestinationUtil.validateDestination(accessor.getDestination());

            final SessionPrincipal sessionPrincipal = (SessionPrincipal) accessor.getUser();// sessionPrincipal 추출 
            
            // 다른 구독이 없는지 검증
            sessionRegistryService.validateSessionNotSubscribedAnyDestination(sessionPrincipal.getName());
            
            final String roomId = DestinationUtil.extractRoomIdByDestination(accessor.getDestination());    // roomId 추출
            
            // 구독 권한 검증
            if (!roomService.isUserParticipant(sessionPrincipal.getUserId(), roomId)) {
                throw new RuntimeException("User does not have access to room: " + roomId);
            }
        }

        return message;
    }

}
