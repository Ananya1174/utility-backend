package com.utility.auth.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "notification.exchange";
    public static final String ACCOUNT_ROUTING_KEY = "account.approved";
    public static final String ACCOUNT_REJECTED_KEY = "account.rejected";
    public static final String CONSUMER_APPROVED_KEY = "consumer.approved";
    public static final String PASSWORD_RESET_KEY = "password.reset";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}