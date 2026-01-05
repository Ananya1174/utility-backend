package com.utility.billing.feign;

import lombok.Data;
import com.utility.billing.model.UtilityType;

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