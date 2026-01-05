package com.utility.meter.dto;

import com.utility.meter.model.UtilityType;
import lombok.Data;

@Data
public class MeterReadingResponse {

    private String id;
    private String consumerId;
    private String connectionId;
    private UtilityType utilityType;
    private String meterNumber;

    private long previousReading;
    private long currentReading;
    private long consumptionUnits;

    private int readingMonth;
    private int readingYear;
}