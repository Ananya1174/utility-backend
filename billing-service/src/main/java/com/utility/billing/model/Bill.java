package com.utility.billing.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "bills")
public class Bill {

    @Id
    private String id;

    private String consumerId;
    private String connectionId;

    private UtilityType utilityType;
    private String tariffPlan;          

    private int billingMonth;          
    private int billingYear;           

    private long consumptionUnits;

    private double energyCharge;
    private double fixedCharge;
    private double tax;
    private double penalty;            
    private double totalAmount;

    private BillStatus status;
    
    private LocalDate billDate;
    private LocalDate dueDate;
}