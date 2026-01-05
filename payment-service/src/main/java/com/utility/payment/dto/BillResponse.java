package com.utility.payment.dto;

import java.time.LocalDate;

import com.utility.payment.model.BillStatus;
import com.utility.payment.model.UtilityType;
import lombok.Data;

@Data
public class BillResponse {

    private String id;
    private String consumerId;
    private String connectionId;

    private UtilityType utilityType;
    private String tariffPlan;

    private int billingMonth;
    private int billingYear;

    private double energyCharge;
    private double tax;
    private double penalty;

    private long consumptionUnits;

    private double totalAmount;
    private double payableAmount;

    private BillStatus status;
    private LocalDate dueDate;
}