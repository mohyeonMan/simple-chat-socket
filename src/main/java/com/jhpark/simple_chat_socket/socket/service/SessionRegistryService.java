package com.jhpark.simple_chat_socket.socket.service;

import java.util.Set;

import org.springframework.messaging.simp.user.SimpSession;
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

    public Set<SimpUser> getUsers() {
        return userRegistry.getUsers();
    }

    public void validateUserSubscription(final Long userId, final String roomId) {
        if (!isUserSubscribed(userId, roomId)) {
            throw new RuntimeException("User is not subscribed to the room.");
        }
    }

    public boolean isUserSubscribed(final Long userId, final String roomId) {
        final SimpUser user = userRegistry.getUser(String.valueOf(userId));

        if (user == null || user.getSessions().isEmpty()) {
            return false;
        }

        for (SimpSession session : user.getSessions()) {
            for (SimpSubscription subscription : session.getSubscriptions()) {
                final String destination = subscription.getDestination();

                if (DestinationUtil.isValiedDestination(destination) &&
                        roomId.equals(DestinationUtil.extractRoomIdByDestination(destination)))
                    return true;
            }
        }

        return false;
    }

}
