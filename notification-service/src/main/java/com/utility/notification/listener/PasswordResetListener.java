package com.utility.notification.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.utility.common.dto.event.PasswordResetEvent;
import com.utility.notification.config.RabbitMQConfig;
import com.utility.notification.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.PASSWORD_RESET_QUEUE)
    public void handlePasswordReset(PasswordResetEvent event) {

        if (event.getEmail() == null || event.getResetToken() == null) {
            return;
        }

        emailService.sendPasswordResetEmail(
                event.getEmail(),
                event.getResetToken()
        );
    }
}