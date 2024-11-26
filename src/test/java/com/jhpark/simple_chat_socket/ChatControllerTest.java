// package com.jhpark.simple_chat_socket;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.Nested;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.listener.ChannelTopic;
// import org.springframework.messaging.simp.stomp.StompFrameHandler;
// import org.springframework.messaging.simp.stomp.StompHeaders;
// import org.springframework.messaging.simp.stomp.StompSession;
// import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
// import org.springframework.web.socket.sockjs.client.SockJsClient;
// import org.springframework.web.socket.sockjs.client.WebSocketTransport;

// import com.jhpark.simple_chat_socket.socket.config.WebSocketConfig;

// import org.springframework.web.socket.client.standard.StandardWebSocketClient;
// import org.springframework.web.socket.messaging.WebSocketStompClient;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.lang.reflect.Type;
// import java.util.List;
// import java.util.concurrent.CompletableFuture;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 환경의 독립성을 위해 랜덤
// public class ChatControllerTest {

//     // @Mock
//     // private RedisTemplate<String, Object> redisTemplate;

//     // @Mock
//     // private ChannelTopic topic;

//     private WebSocketStompClient stompClient;

//     @Value("${local.server.port}")
//     private int port; // 랜덤 포트 사용

//     private static final String WEB_SOCKET_URI = "ws://localhost:";

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
        
//         // 최신 WebSocket 클라이언트 초기화
//         WebSocketTransport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
//         stompClient = new WebSocketStompClient(new SockJsClient(List.of(webSocketTransport)));
//     }

//     @Nested
//     class WebSocketCommunicationTest {

//         @Test
//         void testMessageExchangeBetweenClients() throws Exception {
//             String testMessage = "웹소켓 테스트 메시지";
//             CompletableFuture<StompSession> client2SessionFuture = new CompletableFuture<>();

//             // 첫 번째 클라이언트 연결 및 메시지 전송
//             CompletableFuture<StompSession> client1SessionFuture = new CompletableFuture<>();

//             // 첫 번째 클라이언트 연결
//             stompClient.connectAsync(WEB_SOCKET_URI + port + WebSocketConfig.END_POINT, new StompSessionHandlerAdapter() {
//                 @Override
//                 public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                     // 첫 번째 클라이언트의 세션을 완료
//                     client1SessionFuture.complete(session);

//                     // 두 번째 클라이언트가 구독을 준비한 후 메시지를 보내기 위해 첫 번째 클라이언트가 전송
//                     try {
//                         // 두 번째 클라이언트 구독이 준비되었을 때까지 기다림
//                         client2SessionFuture.get();

//                         // 메시지를 첫 번째 클라이언트가 전송
//                         session.send(WebSocketConfig.DESTINATION_PREFIX + "/chat", testMessage);
//                     } catch (Exception e) {
//                         e.printStackTrace();
//                     }
//                 }
//             }).get();

//             // 두 번째 클라이언트 연결 및 구독
//             stompClient.connectAsync(WEB_SOCKET_URI + port + WebSocketConfig.END_POINT, new StompSessionHandlerAdapter() {
//                 @Override
//                 public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                     // 두 번째 클라이언트의 세션을 완료
//                     client2SessionFuture.complete(session);

//                     // 메시지를 /topic/chat을 통해 수신할 때의 동작 정의
//                     session.subscribe(WebSocketConfig.TOPIC_PREFIX + "/chat", new StompFrameHandler() {
//                         @Override
//                         public Type getPayloadType(StompHeaders headers) {
//                             return String.class;
//                         }

//                         @Override
//                         public void handleFrame(StompHeaders headers, Object payload) {
//                             // 두 번째 클라이언트가 메시지를 수신하면 처리
//                             assertEquals(testMessage, payload);
//                         }
//                     });
//                 }
//             }).get();

//             // 첫 번째 클라이언트가 연결되고, 두 번째 클라이언트가 구독을 완료한 후 메시지가 전송되도록 함
//             client1SessionFuture.get(); // 첫 번째 클라이언트 세션 대기
//         }
//     }
// }