package com.utility.billing.feign;

import java.util.List;

import org.springframework.stereotype.Component;

import com.utility.billing.exception.ApiException;

import org.springframework.http.HttpStatus;

@Component
public class MeterReadingFallback implements MeterReadingClient {

    @Override
    public MeterReadingResponse getLatest(String connectionId) {

        throw new ApiException(
                "MeterReading service unavailable (getLatest)",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @Override
    public List<MeterReadingResponse> getByConnection(String connectionId) {

        return List.of();
    }
}