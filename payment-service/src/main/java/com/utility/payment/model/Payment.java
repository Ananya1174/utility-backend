package com.utility.payment.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String billId;
    private String consumerId;

    private double amount;

    private PaymentMode mode;
    private PaymentStatus status;

    private String otp;
    private Instant otpExpiry;

    private String remarks;

    private String transactionId;
    private String invoiceId;

    private int billingMonth;
    private int billingYear;

    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
}