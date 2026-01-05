package com.utility.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "notification.exchange";
    public static final String PAYMENT_OTP_QUEUE = "payment.otp.queue";
    public static final String PAYMENT_OTP_KEY = "payment.otp.generated";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue paymentOtpQueue() {
        return QueueBuilder.durable(PAYMENT_OTP_QUEUE).build();
    }

    @Bean
    public Binding paymentOtpBinding() {
        return BindingBuilder
                .bind(paymentOtpQueue())
                .to(notificationExchange())
                .with(PAYMENT_OTP_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}