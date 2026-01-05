package com.utility.consumer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.utility.consumer.dto.request.ConsumerRequestDTO;
import com.utility.consumer.dto.response.ConsumerResponseDTO;
import com.utility.consumer.exception.ApiException;
import com.utility.consumer.model.Consumer;
import com.utility.consumer.repository.ConsumerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final ConsumerRepository consumerRepository;
    private static final String CONSUMER_NOT_FOUND = "Consumer not found";
    

    public ConsumerResponseDTO createConsumer(ConsumerRequestDTO dto) {

        if (consumerRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        if (consumerRepository.existsByMobileNumber(dto.getMobileNumber())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Mobile number already exists"
            );
        }

        Consumer consumer = new Consumer();
        consumer.setFullName(dto.getFullName());
        consumer.setEmail(dto.getEmail());
        consumer.setMobileNumber(dto.getMobileNumber());
        consumer.setAddress(dto.getAddress());
        consumer.setCreatedAt(LocalDateTime.now());

        return mapToDTO(consumerRepository.save(consumer));
    }

    public ConsumerResponseDTO getConsumer(String id) {
        Consumer consumer = consumerRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        CONSUMER_NOT_FOUND
                ));
        return mapToDTO(consumer);
    }

    public List<ConsumerResponseDTO> getAllConsumers() {
        return consumerRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ConsumerResponseDTO updateConsumer(String id, ConsumerRequestDTO dto) {

        Consumer consumer = consumerRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        CONSUMER_NOT_FOUND
                ));

        consumer.setFullName(dto.getFullName());
        consumer.setAddress(dto.getAddress());
        consumer.setUpdatedAt(LocalDateTime.now());

        return mapToDTO(consumerRepository.save(consumer));
    }

    public void deactivateConsumer(String id) {

        Consumer consumer = consumerRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        CONSUMER_NOT_FOUND
                ));

        consumer.setActive(false);
        consumer.setUpdatedAt(LocalDateTime.now());
        consumerRepository.save(consumer);
    }

    private ConsumerResponseDTO mapToDTO(Consumer c) {
        return new ConsumerResponseDTO(
                c.getId(),
                c.getFullName(),
                c.getEmail(),
                c.getMobileNumber(),
                c.getAddress(),
                c.isActive()
        );
    }
}