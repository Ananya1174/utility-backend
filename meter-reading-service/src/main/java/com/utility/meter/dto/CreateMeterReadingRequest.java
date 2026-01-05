package com.utility.meter.dto;

import com.utility.meter.model.UtilityType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMeterReadingRequest {

    @NotBlank
    private String consumerId;

    @NotBlank
    private String connectionId;

    @NotNull
    private UtilityType utilityType;

    @NotBlank
    private String meterNumber;

    @Min(0)
    private long currentReading;

    @Min(1)
    @Max(12)
    private int readingMonth;   

    @Min(2000)
    private int readingYear;    
}