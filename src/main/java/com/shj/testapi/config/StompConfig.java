package com.shj.testapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    // 주의할점은, 여기의 @Value는 'springframework.beans.factory.annotation.Value'소속이다! lombok의 @Value와 착각하지 말자!
    @Value("${spring.rabbitmq.username}")
    private String rabbitUser;
    @Value("${spring.rabbitmq.password}")
    private String rabbitPw;
    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;
    @Value("${spring.rabbitmq.stomp-port}")
    private int stompPort;
    private String rabbitVirtualHost = "/";


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 구독 url (topic을 구독) (즉, 현재 프론트엔드의 채팅방을 의미하여, 프론트엔드가 백엔드에게 응답받을 주소임. = 프론트엔드 주소)
        registry.enableStompBrokerRelay("/exchange")
                .setClientLogin(rabbitUser)
                .setClientPasscode(rabbitPw)
                .setSystemLogin(rabbitUser)
                .setSystemPasscode(rabbitPw)
                .setRelayHost(rabbitHost)
                .setRelayPort(stompPort)
                .setVirtualHost(rabbitVirtualHost);

        // 메시지 발행 url (즉, 프론트엔드가 백엔드로 요청할 주소임. 백엔드에서 @MessageMapping로 요청받음. = 백엔드 주소)
        registry.setPathMatcher(new AntPathMatcher("."));  // 예시로, url을 'chat/Room/3 -> chat.Room.3'으로 참조하기 위한 설정임.
        registry.setApplicationDestinationPrefixes("/pub");  // '/pub/~'는 @MessageMapping에서 받아냄.
    }

    // 웹소켓 핸드셰이크 커넥션을 생성할 경로
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")  // 핸들러를 등록할때 소켓에 접속하기위한 경로 ("/ws")를 함께 설정.
                .setAllowedOriginPatterns("*");  // 다른곳에서 접속이 가능하도록 setAllowedOrigins("*")을 붙여 cors문제를 해결.
        // .withSockJS();

//        registry.setErrorHandler(new JwtStompExceptionHandler());  // 소켓 통신 중, 예외가 발생했을 때 JwtStompExceptionHandler로 제어권이 넘어간다.
    }

//    // 프론트엔드->백엔드로 실시간 Stomp 요청을 주었을때 채널 인터셉터
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new JwtChannelInterceptor());
//    }
}