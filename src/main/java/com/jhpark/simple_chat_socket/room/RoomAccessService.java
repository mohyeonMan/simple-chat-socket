package com.jhpark.simple_chat_socket.room;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomAccessService {

    private final RestTemplate restTemplate;
    
    public boolean hasAccessToRoom(final Long userId, String roomId) {
        log.info("isRoomMemeber roomId={}, userId={}", roomId, userId);
        return true;
    }
    
}
