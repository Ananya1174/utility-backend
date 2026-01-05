package com.utility.notification.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.utility.common.dto.event.BillDueReminderEvent;
import com.utility.notification.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillReminderListener {

    private final EmailService emailService;

    @RabbitListener(queues = "bill.reminder.queue")
    public void handleBillReminder(BillDueReminderEvent event) {

        if (event.getEmail() == null || event.getEmail().isBlank()) {
            return;
        }
        emailService.sendPaymentReminderEmail(
                event.getEmail(),
                event
        );
    }
}