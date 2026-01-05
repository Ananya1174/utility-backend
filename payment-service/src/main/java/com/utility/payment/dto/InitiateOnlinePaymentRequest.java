package com.utility.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record InitiateOnlinePaymentRequest(
        @NotBlank String billId,
        @NotBlank String consumerId
) {}