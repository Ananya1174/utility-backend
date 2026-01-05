package com.utility.payment.dto.dashboard;

import com.utility.payment.model.PaymentMode;

public record PaymentModeSummaryDto(
        PaymentMode mode,
        double amount
) {}
