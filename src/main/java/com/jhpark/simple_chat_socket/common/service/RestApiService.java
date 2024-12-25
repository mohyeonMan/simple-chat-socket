package com.jhpark.simple_chat_socket.common.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jhpark.simple_chat_socket.security.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestApiService {

    private final RestTemplate restTemplate;
    private final AuthService authService;

    /**
     * @param url API 엔드포인트 URL
     * @param method HTTP 메서드 (GET, POST 등)
     * @param body 요청 본문 (nullable)
     * @param responseType 응답 타입 (ParameterizedTypeReference 사용)
     * @param <T> 응답 데이터 타입
     * @return 응답 데이터
     */
    public <T> T sendRequest(
        final String url,
        final HttpMethod method,
        final HttpHeaders headers,
        final Object body,
        final ParameterizedTypeReference<T> responseType
    ) {

        // HttpHeaders headers = createHeadersWithAuthorization();

        // 요청 생성
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        // REST API 요청
        ResponseEntity<T> response = restTemplate.exchange(
            url,
            method,
            requestEntity,
            responseType
        );

        return response.getBody(); // 응답 데이터 반환
    }

    /**
     * @return HttpHeaders
     */
    public HttpHeaders createHeadersWithAuthorization(final String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

}

