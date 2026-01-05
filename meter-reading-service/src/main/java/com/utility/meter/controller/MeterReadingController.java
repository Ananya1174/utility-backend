package com.utility.meter.controller;

import com.utility.meter.dto.CreateMeterReadingRequest;
import com.utility.meter.dto.MeterReadingResponse;
import com.utility.meter.exception.ApiException;
import com.utility.meter.service.MeterReadingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meter-readings")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingService service;

    @PostMapping
    @PreAuthorize("hasRole('BILLING_OFFICER')")
    public ResponseEntity<MeterReadingResponse> add(
            @Valid @RequestBody CreateMeterReadingRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addReading(request));
    }

    @GetMapping("/consumer/{consumerId}")
    public ResponseEntity<List<MeterReadingResponse>> byConsumer(
    		@PathVariable("consumerId") String consumerId) {

        List<MeterReadingResponse> readings =
                service.getByConsumer(consumerId);

        if (readings.isEmpty()) {
            throw new ApiException(
                    "No meter readings found for consumer",
                    HttpStatus.NOT_FOUND
            );
        }

        return ResponseEntity.ok(readings);
    }

    @GetMapping("/month")
    public ResponseEntity<List<MeterReadingResponse>> byMonth(
    		 @RequestParam("month") int month,
    		 @RequestParam("year") int year) {

        List<MeterReadingResponse> readings =
                service.getByMonth(month, year);

        if (readings.isEmpty()) {
            throw new ApiException(
                    "No meter readings found for the given month",
                    HttpStatus.NOT_FOUND
            );
        }

        return ResponseEntity.ok(readings);
    }

    @GetMapping("/latest/{connectionId}")
    public MeterReadingResponse latest(
    		@PathVariable("connectionId") String connectionId) {

        return service.getLatest(connectionId);
    }

    @GetMapping("/connection/{connectionId}")
    public ResponseEntity<List<MeterReadingResponse>> byConnection(
    		 @PathVariable("connectionId") String connectionId) {

        List<MeterReadingResponse> readings =
                service.getByConnection(connectionId);

        if (readings.isEmpty()) {
            throw new ApiException(
                    "No meter readings found for connection",
                    HttpStatus.NOT_FOUND
            );
        }

        return ResponseEntity.ok(readings);
    }
    
}