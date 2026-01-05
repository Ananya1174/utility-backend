package com.utility.notification.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.utility.common.dto.event.PaymentOtpEvent;
import com.utility.notification.config.RabbitMQConfig;
import com.utility.notification.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentOtpListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_OTP_QUEUE)
    public void handlePaymentOtp(PaymentOtpEvent event) {

    	System.out.println("✅ OTP EVENT RECEIVED FOR: " + event.getEmail());
        emailService.sendPaymentOtpEmail(
                event.getEmail(),
                event.getOtp(),
                event.getValidMinutes()
        );
    }
}