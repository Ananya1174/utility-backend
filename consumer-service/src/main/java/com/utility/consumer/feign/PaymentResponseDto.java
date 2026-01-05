package com.utility.consumer.feign;

import java.time.LocalDateTime;
import lombok.Data;
@Data

public class PaymentResponseDto {

    private String paymentId;
    private String billId;
    private String consumerId;
    private int billingMonth;
    private int billingYear;
    private double amount;
    private String mode;
    private String status;
    private String transactionId;
    private String invoiceId;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;

}