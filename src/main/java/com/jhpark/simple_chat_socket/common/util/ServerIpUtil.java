package com.jhpark.simple_chat_socket.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerIpUtil {

    public static String getServerIp() {
        try {
            Process process = Runtime.getRuntime().exec("hostname -i");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.readLine().trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch server IP", e);
        }
    }
}