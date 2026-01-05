package com.utility.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record OfflinePaymentRequest(
        @NotBlank String billId,
        @NotBlank String consumerId,
        String remarks
) {}