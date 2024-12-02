package com.jhpark.simple_chat_socket.common.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerIpUtil {

    private static final String TOKEN_URL = "http://169.254.169.254/latest/api/token";
    private static final String PRIVATE_IP_URL = "http://169.254.169.254/latest/meta-data/local-ipv4";
    private final RestTemplate restTemplate;

    public String getServerIp() {
        if (isAwsEnvironment()) {
            log.info("AWS Environment");
            return getPrivateIp();
        } else {
            log.info("Local Environment");
            return "localhost";
        }
    }

    public boolean isAwsEnvironment() {
        try {
            String token = getMetadataToken();
            return token != null && !token.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPrivateIp() {
        try {
            String token = getMetadataToken();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-aws-ec2-metadata-token", token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(PRIVATE_IP_URL, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve private IP from AWS metadata service", e);
        }
    }

    private String getMetadataToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-aws-ec2-metadata-token-ttl-seconds", "21600");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.PUT, entity, String.class);
        return response.getBody();
    }
}
