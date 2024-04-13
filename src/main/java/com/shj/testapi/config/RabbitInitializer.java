package com.shj.testapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.stereotype.Component;

@Component
public class RabbitInitializer {

    private final AmqpAdmin amqpAdmin;
    private final RabbitConfig rabbitConfig;

    public RabbitInitializer(AmqpAdmin amqpAdmin, RabbitConfig rabbitConfig) {
        this.amqpAdmin = amqpAdmin;
        this.rabbitConfig = rabbitConfig;
    }

    @PostConstruct
    public void init() {
        amqpAdmin.declareExchange(rabbitConfig.exchange());
    }
}