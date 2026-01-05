package com.utility.consumer.service;

import com.utility.consumer.dto.request.ConsumerRequestDTO;
import com.utility.consumer.exception.ApiException;
import com.utility.consumer.model.Consumer;
import com.utility.consumer.repository.ConsumerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @Mock
    private ConsumerRepository consumerRepository;

    @InjectMocks
    private ConsumerService service;

    @Test
    void createConsumer_success() {

        ConsumerRequestDTO dto = new ConsumerRequestDTO();
        dto.setEmail("a@gmail.com");
        dto.setMobileNumber("999");

        when(consumerRepository.existsByEmail("a@gmail.com"))
                .thenReturn(false);
        when(consumerRepository.existsByMobileNumber("999"))
                .thenReturn(false);
        when(consumerRepository.save(any()))
                .thenReturn(new Consumer());

        service.createConsumer(dto);
    }
    @Test
    void updateConsumer_success() {

        Consumer consumer = new Consumer();
        consumer.setId("C1");

        ConsumerRequestDTO dto = new ConsumerRequestDTO();
        dto.setFullName("Updated");
        dto.setAddress("BLR");

        when(consumerRepository.findById("C1"))
                .thenReturn(Optional.of(consumer));

        when(consumerRepository.save(any()))
                .thenReturn(consumer);

        service.updateConsumer("C1", dto);
    }
    @Test
    void deactivateConsumer_success() {

        Consumer consumer = new Consumer();
        consumer.setId("C1");
        consumer.setActive(true);

        when(consumerRepository.findById("C1"))
                .thenReturn(Optional.of(consumer));

        service.deactivateConsumer("C1");

        assertFalse(consumer.isActive());
    }

    @Test
    void getConsumer_notFound() {

        when(consumerRepository.findById("C1"))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.getConsumer("C1"));
    }

    @Test
    void getAllConsumers_success() {

        when(consumerRepository.findAll())
                .thenReturn(List.of(new Consumer()));

        assertEquals(1, service.getAllConsumers().size());
    }
}