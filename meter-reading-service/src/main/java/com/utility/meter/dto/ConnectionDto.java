package com.utility.meter.dto;

import com.utility.meter.model.UtilityType;
import lombok.Data;

@Data
public class ConnectionDto {

    private String id;
    private String consumerId;
    private UtilityType utilityType;
    private String meterNumber;
}