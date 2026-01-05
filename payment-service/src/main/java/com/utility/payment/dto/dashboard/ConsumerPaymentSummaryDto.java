package com.utility.payment.dto.dashboard;

public record ConsumerPaymentSummaryDto(
        String consumerId,
        long paymentCount,
        double totalPaid
) {}