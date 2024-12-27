package com.jhpark.simple_chat_socket.session.service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_socket.socket.util.DestinationUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Service
public class SessionRegistryService {

    private final SimpUserRegistry userRegistry;

    public SessionRegistryService(@Lazy SimpUserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    public void validateSessionNotSubscribedAnyDestination(final String sessionName) {
        final SimpUser user = userRegistry.getUser(sessionName);
    
        if (user == null) {
            return;
        }
    
        boolean isAlreadySubscribed = user.getSessions().stream()
            .flatMap(simpSession -> simpSession.getSubscriptions().stream())
            .findAny() // 하나라도 있으면 이미 구독 중
            .isPresent();
    
        if (isAlreadySubscribed) {
            throw new RuntimeException("User is already subscribed to a destination.");
        }
    
        log.debug("User is not subscribed to any destination.");
    }

    //메시지 전송시 발신자의 구독검증
    public void validateSessionSubscription(final String sessionName, final Long roomId) {
        if (!isSessionSubscribedRoom(sessionName, roomId)) {
            throw new RuntimeException("User is not subscribed to the room.");
        }
    }

    public boolean isSessionSubscribedRoom(final String sessionName, final Long roomId) {
        final SimpUser user = userRegistry.getUser(sessionName);
    
        if (user == null) {
            return false; // 사용자 또는 해당 세션 정보가 없으면 false
        }
    
        return user.getSessions().stream()
            .flatMap(simpSession -> simpSession.getSubscriptions().stream())
            .filter(Objects::nonNull)
            .map(SimpSubscription::getDestination)
            .anyMatch(destination -> roomId.equals(DestinationUtil.extractRoomIdByDestination(destination)));
    }
    

    public Set<String> getSessionsNotMatch(final Long userId, final Set<String> sessionIds, final String roomId){
        final SimpUser user = userRegistry.getUser(String.valueOf(userId));

        if (user == null || !user.hasSessions()) {
            return new HashSet<>(sessionIds);
        }
    
        return sessionIds.stream()
                .map(user::getSession)
                .filter(session -> session == null ||
                        session.getSubscriptions().stream()
                                .filter(Objects::nonNull)
                                .map(SimpSubscription::getDestination)
                                .anyMatch(destination -> !roomId
                                        .equals(DestinationUtil.extractRoomIdByDestination(destination))))
                .collect(HashSet::new, (set, session) -> set.add(session.getId()), HashSet::addAll);
                
    }
    

}
