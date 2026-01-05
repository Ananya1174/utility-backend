package com.utility.meter.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "meter_readings")
public class MeterReading {

    @Id
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

    private LocalDate readingDate;
    private LocalDateTime createdAt;
}