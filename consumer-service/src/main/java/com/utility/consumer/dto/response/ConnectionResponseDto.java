package com.utility.consumer.dto.response;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.enums.UtilityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor       

@AllArgsConstructor
public class ConnectionResponseDto {

    private String id;
    private UtilityType utilityType;
    private String consumerId;
    private String meterNumber;
    private ConnectionRequestStatus status;
    private String tariffPlan;
    private boolean active;
    private LocalDateTime activatedAt;
    private LocalDateTime requestedAt;

}