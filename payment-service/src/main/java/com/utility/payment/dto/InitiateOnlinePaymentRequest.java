package com.utility.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record InitiateOnlinePaymentRequest(
        @NotBlank String billId,
        @NotBlank String consumerId
) {}