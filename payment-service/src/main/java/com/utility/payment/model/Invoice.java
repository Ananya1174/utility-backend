package com.utility.payment.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "invoices")
public class Invoice {

    @Id
    private String id;

    private String billId;
    private String paymentId;
    private String consumerId;
    private int billingMonth;
    private int billingYear;
    private double energyCharge;


    private double amountPaid;
    private double tax;
    private double penalty;
    private double totalAmount;
    private LocalDateTime invoiceDate;

    private String invoiceNumber;
}