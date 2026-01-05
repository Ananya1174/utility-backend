package com.utility.consumer.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.enums.UtilityType;

import lombok.Data;

@Document(collection = "connection_requests")
@Data
public class ConnectionRequest {

    @Id
    private String id;

    private String consumerId;

    private UtilityType utilityType;

   
    private String tariffPlanCode;

    private ConnectionRequestStatus status; 

    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;

    private String reviewedBy;
    private String rejectionReason;
}