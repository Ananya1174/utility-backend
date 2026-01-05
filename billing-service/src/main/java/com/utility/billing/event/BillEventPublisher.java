package com.utility.billing.event;

import com.utility.billing.config.RabbitConfig;
import com.utility.common.dto.event.BillDueReminderEvent;
import com.utility.common.dto.event.BillGeneratedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(BillGeneratedEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.BILL_ROUTING_KEY,
                event
        );
    }
    public void publishDueReminder(BillDueReminderEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "bill.due.reminder",
                event
        );
    }
}