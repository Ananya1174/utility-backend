package com.utility.common.dto.event;

import lombok.Data;

@Data
public class PaymentOtpEvent {

    private String email;
    private String otp;
    private int validMinutes;
}