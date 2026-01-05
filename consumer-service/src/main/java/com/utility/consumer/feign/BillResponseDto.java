package com.utility.consumer.feign;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
@Data
public class BillResponseDto {

    private String id;
    private String consumerId;
    private String connectionId;

    private String utilityType;  
    private String tariffPlan;

    private int billingMonth;
    private int billingYear;

    private double penalty;
    private double tax;
    private long consumptionUnits;

    private double totalAmount;
    private double payableAmount;

    private String status;       
    private LocalDate dueDate;

   
}