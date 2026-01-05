package com.utility.consumer.dto.dashboard;

import java.time.LocalDateTime;

public record ConsumerDashboardSummaryDto(
        int activeUtilities,
        int pendingRequests,
        int totalBills,
        int unpaidBills,
        double totalOutstanding,
        Double lastPaymentAmount,
        LocalDateTime lastPaymentDate
) {}