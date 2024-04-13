package com.shj.testapi.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    // exchange와 queue는 1:n 관계로 해석해도 괜찮을듯 하다.
    // queue는 exchange와 바인딩되어 한세트로 묶여있음.
    public static final String CHAT_QUEUE_NAME = "chat.queue";
    public static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    public static final String CHAT_ROUTING_KEY = "room.*";  // 참고: 'https://zamezzz.tistory.com/326'

    public static final String OTHER_QUEUE_NAME = "other.queue";
    public static final String OTHER_EXCHANGE_NAME = "other.exchange";
    public static final String OTHER_ROUTING_KEY = "other.*";

    // 주의할점은, 여기의 @Value는 'springframework.beans.factory.annotation.Value'소속이다! lombok의 @Value와 착각하지 말자!
    @Value("${spring.rabbitmq.username}")
    private String rabbitUser;
    @Value("${spring.rabbitmq.password}")
    private String rabbitPw;
    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;
    @Value("${spring.rabbitmq.rabbitmq-port}")
    private int rabbitPort;
    private String rabbitVirtualHost = "/";


    // RabbitAdmin을 사용하면 RabbitMQ 서버에 Exchange, Queue, Binding을 등록할 수 있다.
    // 이는 RabbitTemplate을 사용하여 RabbitMQ 서버에 접근한다.
    @Bean
    public AmqpAdmin amqpAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());

        rabbitAdmin.declareExchange(chatExchange());
        rabbitAdmin.declareExchange(otherExchange());

        rabbitAdmin.declareQueue(chatQueue());
        rabbitAdmin.declareQueue(otherQueue());

        rabbitAdmin.declareBinding(chatBinding(chatQueue(), chatExchange()));
        rabbitAdmin.declareBinding(otherBinding(otherQueue(), otherExchange()));

        return rabbitAdmin;
    }

    // Queue 등록
    @Bean
    public Queue chatQueue() {
        return new Queue(CHAT_QUEUE_NAME, true);
    }
    @Bean
    public Queue otherQueue() {
        return new Queue(OTHER_QUEUE_NAME, true);
    }

    // Exchange 등록
    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE_NAME,true,false);
    }
    @Bean
    public TopicExchange otherExchange() {
        return new TopicExchange(OTHER_EXCHANGE_NAME,true,false);
    }

    // Exchange와 Queue 바인딩
    @Bean
    public Binding chatBinding(Queue queue, TopicExchange exchange) {  // 메시지가 소비자(Consumer)가 없는 경우에도 메시지를 보존할 수 있어 중요한 메시지의 유실을 방지할 수 있음.
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(CHAT_ROUTING_KEY);
    }
    @Bean
    public Binding otherBinding(Queue queue, TopicExchange exchange) {  // 메시지가 소비자(Consumer)가 없는 경우에도 메시지를 보존할 수 있어 중요한 메시지의 유실을 방지할 수 있음.
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(OTHER_ROUTING_KEY);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {  // 이건 안적어도 되지않나 싶다(?)
        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    // RabbitMQ와의 메시지 통신을 담당하는 클래스
    // messageConverter를 커스터마이징 하기 위해 Bean 새로 등록.
    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
    // RabbitMQ와의 연결을 관리하는 클래스
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(rabbitHost);
        factory.setVirtualHost(rabbitVirtualHost);
        factory.setUsername(rabbitUser);
        factory.setPassword(rabbitPw);
        factory.setPort(rabbitPort);
        return factory;
    }

    // 메시지를 JSON형식으로 직렬화하고 역직렬화하는데 사용되는 변환기
    // RabbitMQ 메시지를 JSON형식으로 보내고 받을 수 있음
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(){
        // LocalDateTime serializable을 위해
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.registerModule(dateTimeModule());

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        return converter;
    }
    @Bean
    public Module dateTimeModule(){
        return new JavaTimeModule();
    }
    /*
    < Chat LocalDateTime example / 2024-04-09T02:10:29.415171 / 2024.04.09 02H:10M:29.415171S >
    {
        "createdTime": [
            2024,
            4,
            9,
            2,
            10,
            29,
            415171000
        ]
    }
     */
}
