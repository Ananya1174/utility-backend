package com.utility.consumer.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utility.consumer.dto.request.ConsumerRequestDTO;
import com.utility.consumer.dto.response.ConsumerResponseDTO;
import com.utility.consumer.service.ConsumerService;

@RestController
@RequestMapping("/consumers")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsumerResponseDTO> create(
            @Valid @RequestBody ConsumerRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consumerService.createConsumer(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsumerResponseDTO> get(@PathVariable("id") String id) {
        return ResponseEntity.ok(consumerService.getConsumer(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConsumerResponseDTO>> getAll() {
        return ResponseEntity.ok(consumerService.getAllConsumers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsumerResponseDTO> update(
    		@PathVariable("id") String id,
            @Valid @RequestBody ConsumerRequestDTO dto) {

        return ResponseEntity.ok(consumerService.updateConsumer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable("id") String id) {
        consumerService.deactivateConsumer(id);
        return ResponseEntity.noContent().build();
    }
}
