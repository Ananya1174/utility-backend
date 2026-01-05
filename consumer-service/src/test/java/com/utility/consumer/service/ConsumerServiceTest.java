package com.utility.consumer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.utility.consumer.dto.request.ConsumerRequestDTO;
import com.utility.consumer.dto.response.ConsumerResponseDTO;
import com.utility.consumer.exception.ApiException;
import com.utility.consumer.model.Consumer;
import com.utility.consumer.repository.ConsumerRepository;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @Mock
    private ConsumerRepository consumerRepository;

    @InjectMocks
    private ConsumerService service;

    // ---------- CREATE CONSUMER ----------

    @Test
    void createConsumer_success() {

        ConsumerRequestDTO dto = new ConsumerRequestDTO();
        dto.setFullName("Ananya");
        dto.setEmail("a@gmail.com");
        dto.setMobileNumber("999");
        dto.setAddress("BLR");

        Consumer savedConsumer = new Consumer();
        savedConsumer.setId("C1");
        savedConsumer.setEmail("a@gmail.com");
        savedConsumer.setMobileNumber("999");
        savedConsumer.setActive(true);

        when(consumerRepository.existsByEmail("a@gmail.com"))
                .thenReturn(false);
        when(consumerRepository.existsByMobileNumber("999"))
                .thenReturn(false);
        when(consumerRepository.save(any()))
                .thenReturn(savedConsumer);

        ConsumerResponseDTO response = service.createConsumer(dto);

        assertNotNull(response);
        assertEquals("C1", response.getId());
        assertEquals("a@gmail.com", response.getEmail());
    }
    @Test
    void updateConsumer_notFound_lambdaCovered() {

        ConsumerRequestDTO dto = new ConsumerRequestDTO();
        dto.setFullName("X");
        dto.setAddress("Y");

        when(consumerRepository.findById("C404"))
                .thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> service.updateConsumer("C404", dto));

        assertEquals("Consumer not found", ex.getMessage());
    }

    @Test
    void deactivateConsumer_notFound_lambdaCovered() {

        when(consumerRepository.findById("C404"))
                .thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> service.deactivateConsumer("C404"));

        assertEquals("Consumer not found", ex.getMessage());
    }

    // ---------- UPDATE CONSUMER ----------

    @Test
    void updateConsumer_success() {

        Consumer consumer = new Consumer();
        consumer.setId("C1");
        consumer.setFullName("Old Name");
        consumer.setAddress("Old Address");

        ConsumerRequestDTO dto = new ConsumerRequestDTO();
        dto.setFullName("Updated");
        dto.setAddress("BLR");

        when(consumerRepository.findById("C1"))
                .thenReturn(Optional.of(consumer));

        when(consumerRepository.save(any()))
                .thenReturn(consumer);

        ConsumerResponseDTO response = service.updateConsumer("C1", dto);

        assertNotNull(response);
        assertEquals("Updated", response.getFullName());
        assertEquals("BLR", response.getAddress());
    }

    // ---------- DEACTIVATE ----------

    @Test
    void deactivateConsumer_success() {

        Consumer consumer = new Consumer();
        consumer.setId("C1");
        consumer.setActive(true);

        when(consumerRepository.findById("C1"))
                .thenReturn(Optional.of(consumer));

        service.deactivateConsumer("C1");

        assertFalse(consumer.isActive());
        verify(consumerRepository).save(consumer);
    }

    // ---------- GET CONSUMER ----------

    @Test
    void getConsumer_notFound() {

        when(consumerRepository.findById("C1"))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.getConsumer("C1"));
    }

    // ---------- GET ALL ----------

    @Test
    void getAllConsumers_success() {

        when(consumerRepository.findAll())
                .thenReturn(List.of(new Consumer()));

        List<ConsumerResponseDTO> consumers = service.getAllConsumers();

        assertEquals(1, consumers.size());
    }
}