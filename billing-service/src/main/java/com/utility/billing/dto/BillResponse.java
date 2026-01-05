package com.utility.billing.dto;

import com.utility.billing.model.BillStatus;
import com.utility.billing.model.UtilityType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BillResponse {

    private String id;
    private String consumerId;
    private String connectionId;

    private UtilityType utilityType;
    private String tariffPlan;

    private int billingMonth;
    private int billingYear;
    private double penalty; 
    private double tax; 
    private double energyCharge;

    private long consumptionUnits;
    private double totalAmount;
    private double payableAmount;  


    private BillStatus status;
    private LocalDate dueDate;
}