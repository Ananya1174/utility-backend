package com.utility.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmOtpRequest(
        @NotBlank String paymentId,
        @NotBlank String otp
) {}