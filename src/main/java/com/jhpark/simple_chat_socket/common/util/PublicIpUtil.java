package com.jhpark.simple_chat_socket.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class PublicIpUtil {

    private static final String METADATA_URL = "http://169.254.169.254/latest/meta-data/public-ipv4";

    public static String fetchPublicIp() {
        try {
            URL url = new URL(METADATA_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return in.readLine().trim();
            }
        } catch (Exception e) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e2) {
                throw new RuntimeException("Failed to fetch public IP", e);
            }
        }
    }
}