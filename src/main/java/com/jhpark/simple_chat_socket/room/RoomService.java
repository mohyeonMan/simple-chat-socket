package com.jhpark.simple_chat_socket.room;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.common.service.RestApiService;
import com.jhpark.simple_chat_socket.redis.service.RedisService;
import com.jhpark.simple_chat_socket.security.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RestApiService restApiService;
    private final RedisService redisService;
    private final AuthService authService;

    private String roomServerUrl = "http://localhost";

    
    private String getRoomKey(final Long roomId) {
        return "room:" + roomId + ":participants";
    }

    public Set<Long> getRoomUserIds(final Long roomId){
     
        //redis에서 캐시먼저확인
        final Set<Long> userIds = Optional.ofNullable(redisService.get(getRoomKey(roomId)).stream().map(Long::valueOf).collect(Collectors.toSet()))
                .orElseGet(() ->{
                    log.warn("REDIS CASH NOT FOUND : roomId={}",roomId);
                return getRoomUserIdsFromRoomServer(roomId);
            });

        if(userIds ==null || userIds.isEmpty()){
            throw new RuntimeException("사용자를 가져오는데 실패하였습니다.");
        }

        return userIds;
    }

    public Set<Long> getRoomUserIdsFromRoomServer(final Long roomId){
        final Set<Long> userIds = restApiService.sendRequest(roomServerUrl+"/api/room/"+roomId+"/participants", 
            HttpMethod.GET,
            restApiService.createHeadersWithAuthorization(authService.getToken()),
            null, 
            new ParameterizedTypeReference<Set<Long>>() {});

        if(userIds == null || userIds.isEmpty()){
            return Collections.emptySet();
        }

        return userIds;
    }
    
}
