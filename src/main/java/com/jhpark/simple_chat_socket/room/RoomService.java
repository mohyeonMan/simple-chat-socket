package com.jhpark.simple_chat_socket.room;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.common.service.RestApiService;
import com.jhpark.simple_chat_socket.redis.service.RedisService;
import com.jhpark.simple_chat_socket.session.util.SessionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RestApiService restApiService;
    private final RedisService redisService;

    @Value("${room-server-url}")
    private String roomServerUrl = "http://localhost";
    private static String ROOM_API_PREFIX = "/api/room";

    
    private String getRoomKey(final Long roomId) {
        return "room:" + roomId + ":participants";
    }

    public Set<Long> getRoomUserIds(final Long roomId, final Principal principal) {
        // Redis에서 캐시 확인
        Set<Long> userIds = redisService.get(getRoomKey(roomId))
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    
        // 캐시가 없으면 RoomServer에서 가져오기
        if (userIds == null || userIds.isEmpty()) {
            log.warn("REDIS CACHE NOT FOUND: roomId={}", roomId);
            userIds = getRoomUserIdsFromRoomServer(roomId, SessionUtil.extractTokenFromPrincipal(principal));
        }
    
        // 여전히 값이 없으면 예외 발생
        if (userIds == null || userIds.isEmpty()) {
            throw new RuntimeException("GET ROOM PARTICIPANTS FAILED : roomId={}" + roomId);
        }
    
        return userIds;
    }

    private Set<Long> getRoomUserIdsFromRoomServer(final Long roomId, final String token){

        final String roomPathVariable = "/"+roomId;

        final Set<Long> userIds = restApiService.sendRequest(roomServerUrl+ROOM_API_PREFIX+roomPathVariable+"/participants", 
            HttpMethod.GET,
            restApiService.createHeadersWithAuthorization(token),
            null, 
            new ParameterizedTypeReference<Set<Long>>() {});

        if(userIds == null || userIds.isEmpty()){
            return Collections.emptySet();
        }

        return userIds;
    }

    public boolean isUserParticipant(final Long roomId, final String token){

        final String roomPathVariable = "/"+roomId;
        final String requestUrl = roomServerUrl+ROOM_API_PREFIX+roomPathVariable+"/is-participant";

        return restApiService.sendRequest(requestUrl, 
            HttpMethod.GET,
            restApiService.createHeadersWithAuthorization(token),
            null, 
            new ParameterizedTypeReference<Boolean>() {});

    }
    
}
