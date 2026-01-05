package com.utility.billing.dto.dashboard;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class BillsSummaryDto {
    private int month;
    private int year;
    private long totalBills;
    private long paidBills;
    private long unpaidBills;
}