package com.utility.payment.dto.dashboard;

public record PaymentsSummaryDto(
        int month,
        int year,
        long successfulPayments,
        long failedPayments
) {}