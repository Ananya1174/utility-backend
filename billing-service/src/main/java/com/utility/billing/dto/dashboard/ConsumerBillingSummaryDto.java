package com.utility.billing.dto.dashboard;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ConsumerBillingSummaryDto {
    private String consumerId;
    private long totalBills;
    private double totalAmount;
    private double paidAmount;
    private double unpaidAmount;
}
