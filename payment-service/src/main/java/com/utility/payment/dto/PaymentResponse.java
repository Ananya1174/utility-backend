package com.utility.payment.dto;

import com.utility.payment.model.Payment;
import com.utility.payment.model.PaymentMode;
import com.utility.payment.model.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        String paymentId,
        String billId,
        String consumerId,

        int billingMonth,
        int billingYear,

        double amount,
        PaymentMode mode,
        PaymentStatus status,

        String transactionId,
        String invoiceId,
        LocalDateTime createdAt,
        LocalDateTime confirmedAt
) {

    public static PaymentResponse from(
            Payment payment,
            BillResponse bill,
            String invoiceId
    ) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBillId(),
                payment.getConsumerId(),

                bill.getBillingMonth(),
                bill.getBillingYear(),

                payment.getAmount(),
                payment.getMode(),
                payment.getStatus(),

                payment.getTransactionId(),
                invoiceId,
                payment.getCreatedAt(),
                payment.getConfirmedAt()
        );
    }
}