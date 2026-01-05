package com.utility.payment.dto.dashboard;

public record OutstandingSummaryDto(
        double totalBilled,
        double totalPaid,
        double outstandingAmount
) {}