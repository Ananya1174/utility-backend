package com.utility.consumer.messaging;

import com.utility.common.dto.event.ConsumerApprovedEvent;
import com.utility.consumer.model.Consumer;
import com.utility.consumer.repository.ConsumerRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsumerApprovedListener {

    private final ConsumerRepository consumerRepository;

    @RabbitListener(
    	    queues = "consumer.approved.queue",
    	    containerFactory = "rabbitListenerContainerFactory"
    	)
    public void handleConsumerApproved(ConsumerApprovedEvent event) {

                if (consumerRepository.existsById(event.getId())) {
            return;
        }

        Consumer consumer = new Consumer();
        consumer.setId(event.getId()); 
        consumer.setFullName(event.getFullName());
        consumer.setEmail(event.getEmail());
        consumer.setMobileNumber(event.getMobileNumber());
        consumer.setAddress(event.getAddress());
        consumer.setActive(true);
        consumer.setCreatedAt(LocalDateTime.now());

        consumerRepository.save(consumer);
    }
}