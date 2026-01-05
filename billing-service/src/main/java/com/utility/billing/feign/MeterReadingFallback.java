package com.utility.billing.feign;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MeterReadingFallback implements MeterReadingClient {

    @Override
    public MeterReadingResponse getLatest(String connectionId) {
        throw new RuntimeException(
            "MeterReading service unavailable (getLatest)"
        );
    }

    @Override
    public List<MeterReadingResponse> getByConnection(String connectionId) {
        System.err.println(
            "⚠️ FALLBACK HIT for getByConnection: " + connectionId
        );
        return List.of(); // ✅ SAFE
    }
}