package com.utility.notification.listener;

import com.utility.common.dto.event.BillGeneratedEvent;
import com.utility.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillNotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = "bill.notification.queue")
    public void handleBillGenerated(BillGeneratedEvent event) {

        if (event.getEmail() == null || event.getEmail().isBlank()) {
            log.error(
                "Bill email skipped. Email missing. BillId={}, ConsumerId={}",
                event.getBillId(),
                event.getConsumerId()
            );
            return; 
        }

        emailService.sendBillEmail(event.getEmail(), event);
    }
}