package com.utility.payment.dto.dashboard;

public record FailedPaymentSummaryDto(
        long failedCount,
        double failedAmount
) {}