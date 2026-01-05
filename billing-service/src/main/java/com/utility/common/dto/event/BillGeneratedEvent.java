package com.utility.common.dto.event;

import com.utility.billing.model.UtilityType;

import lombok.Data;

@Data
public class BillGeneratedEvent {

    private String billId;
    private String consumerId;
    private String email;

    private double amount;
    private String dueDate;
    private String utilityType;
    private String tariffPlan;

}