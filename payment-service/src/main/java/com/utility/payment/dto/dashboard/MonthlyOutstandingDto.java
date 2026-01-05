package com.utility.payment.dto.dashboard;

public record MonthlyOutstandingDto(
        int month,
        String monthName,
        double totalBilled,
        double totalPaid,
        double outstandingAmount
) {}