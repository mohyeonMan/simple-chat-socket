package com.jhpark.simple_chat_socket.socket.util;

import org.springframework.stereotype.Component;

import com.jhpark.simple_chat_socket.socket.config.WebSocketConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DestinationUtil {

    public static boolean isValiedDestination(final String destination) {
        return destination.startsWith(WebSocketConfig.USER_SUBSCRIBE_PREFIX) &&
            destination.replace(WebSocketConfig.USER_SUBSCRIBE_PREFIX, "").matches("^[1-9]\\d*$");
    }
    

    public static void validateDestination(final String destination) {
        if (!isValiedDestination(destination)) {
            log.error("Invalid destination: " + destination);
            throw new RuntimeException();
        }
    }
    
    public static Long extractRoomIdByDestination(final String destination) {
        try {
            return Long.parseLong(destination.replace(WebSocketConfig.USER_SUBSCRIBE_PREFIX, ""));
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO EXTRACT ROOM ID BY DESTINATION : destination="+destination);
        }
    }

    public static String getBroadcastDestination(final Long roomId){
        return WebSocketConfig.QUEUE_PREFIX + "/" + roomId;
    }


}
