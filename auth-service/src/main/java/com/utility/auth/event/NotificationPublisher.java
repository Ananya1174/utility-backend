package com.utility.auth.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.utility.auth.config.RabbitMQConfig;
import com.utility.common.dto.event.AccountApprovedEvent;
import com.utility.common.dto.event.AccountRejectedEvent;
import com.utility.common.dto.event.ConsumerApprovedEvent;
import com.utility.common.dto.event.PasswordResetEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAccountApproved(AccountApprovedEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ACCOUNT_ROUTING_KEY,
                event
        );
    }
    public void publishAccountRejected(AccountRejectedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ACCOUNT_REJECTED_KEY,
                event
        );
    }
    public void publishConsumerApproved(ConsumerApprovedEvent event) {
    	rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,          
                RabbitMQConfig.CONSUMER_APPROVED_KEY,
                event
        );
    }
    public void publishPasswordReset(PasswordResetEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.PASSWORD_RESET_KEY,
                event
        );
    }
}