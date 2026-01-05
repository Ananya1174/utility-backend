package com.utility.common.dto.event;

import lombok.Data;

@Data
public class BillDueReminderEvent {

    private String billId;
    private String consumerId;
    private String email;
    private double amount;
    private String dueDate;
    private String utilityType;
}