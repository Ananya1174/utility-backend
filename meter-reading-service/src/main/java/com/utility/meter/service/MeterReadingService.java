package com.utility.meter.service;

import com.utility.meter.dto.ConnectionDto;
import com.utility.meter.dto.CreateMeterReadingRequest;
import com.utility.meter.dto.MeterReadingResponse;
import com.utility.meter.exception.ApiException;
import com.utility.meter.feign.ConsumerConnectionClient;
import com.utility.meter.model.MeterReading;
import com.utility.meter.repository.MeterReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeterReadingService {

    private final MeterReadingRepository repository;
    private final ConsumerConnectionClient consumerConnectionClient;
    

    public MeterReadingResponse addReading(CreateMeterReadingRequest request) {
        ConnectionDto connection =
        	    consumerConnectionClient.getConnectionById(request.getConnectionId());

        if (connection == null) {
            throw new ApiException("Connection not found", HttpStatus.BAD_REQUEST);
        }

     // Consumer ownership check
        if (!connection.getConsumerId().equals(request.getConsumerId())) {
            throw new ApiException(
                "Consumer does not own this connection",
                HttpStatus.FORBIDDEN
            );
        }

        // Utility type validation
        if (connection.getUtilityType() != request.getUtilityType()) {
            throw new ApiException(
                "Utility type does not match connection",
                HttpStatus.BAD_REQUEST
            );
        }

        // Meter number validation
        if (!connection.getMeterNumber().equals(request.getMeterNumber())) {
            throw new ApiException(
                "Meter number does not match connection",
                HttpStatus.BAD_REQUEST
            );
        }

        if (repository.existsByConnectionIdAndReadingMonthAndReadingYear(
                request.getConnectionId(),
                request.getReadingMonth(),
                request.getReadingYear()
        )) {
            throw new ApiException(
                    "Meter reading already exists for this month",
                    HttpStatus.BAD_REQUEST
            );
        }

        long previousReading = repository
                .findTopByConnectionIdOrderByReadingYearDescReadingMonthDesc(
                        request.getConnectionId()
                )
                .map(MeterReading::getCurrentReading)
                .orElse(0L);

        if (request.getCurrentReading() < previousReading) {
            throw new ApiException(
                    "Current reading cannot be less than previous reading",
                    HttpStatus.BAD_REQUEST
            );
        }

        long consumptionUnits =
                request.getCurrentReading() - previousReading;

        MeterReading reading = new MeterReading();
        reading.setConsumerId(request.getConsumerId());
        reading.setConnectionId(request.getConnectionId());
        reading.setUtilityType(request.getUtilityType());
        reading.setMeterNumber(request.getMeterNumber());

        reading.setPreviousReading(previousReading);
        reading.setCurrentReading(request.getCurrentReading());
        reading.setConsumptionUnits(consumptionUnits);

        reading.setReadingMonth(request.getReadingMonth());
        reading.setReadingYear(request.getReadingYear());

        reading.setReadingDate(LocalDate.now());
        reading.setCreatedAt(LocalDateTime.now());

        repository.save(reading);

        return map(reading);
    }

    public List<MeterReadingResponse> getByConsumer(String consumerId) {
        return repository.findByConsumerId(consumerId)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<MeterReadingResponse> getByMonth(int month, int year) {
        return repository.findByReadingMonthAndReadingYear(month, year)
                .stream()
                .map(this::map)
                .toList();
    }

    public MeterReadingResponse getLatest(String connectionId) {

        MeterReading reading = repository
                .findTopByConnectionIdOrderByReadingYearDescReadingMonthDesc(
                        connectionId
                )
                .orElseThrow(() ->
                        new ApiException(
                                "No meter reading found for this connection",
                                HttpStatus.NOT_FOUND
                        )
                );

        return map(reading);
    }

    private MeterReadingResponse map(MeterReading r) {
        MeterReadingResponse resp = new MeterReadingResponse();
        resp.setId(r.getId());
        resp.setConsumerId(r.getConsumerId());
        resp.setConnectionId(r.getConnectionId());
        resp.setUtilityType(r.getUtilityType());
        resp.setMeterNumber(r.getMeterNumber());
        resp.setPreviousReading(r.getPreviousReading());
        resp.setCurrentReading(r.getCurrentReading());
        resp.setConsumptionUnits(r.getConsumptionUnits());
        resp.setReadingMonth(r.getReadingMonth());
        resp.setReadingYear(r.getReadingYear());
        return resp;
    }
    public List<MeterReadingResponse> getByConnection(String connectionId) {

        List<MeterReading> readings =
                repository.findByConnectionId(connectionId);

        if (readings.isEmpty()) {
            throw new ApiException(
                    "No meter readings found for this connection",
                    HttpStatus.NOT_FOUND
            );
        }

        return readings.stream()
                .map(this::map)
                .toList();
    }
}