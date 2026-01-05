package com.utility.payment.dto.dashboard;

public record RevenueSummaryDto(
        int month,
        int year,
        double totalRevenue,
        long successfulPayments
) {}