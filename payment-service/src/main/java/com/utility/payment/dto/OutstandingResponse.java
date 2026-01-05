package com.utility.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutstandingResponse {

    private String billId;
    private double totalAmount;
    private double totalPaid;
    private double outstandingAmount;
}