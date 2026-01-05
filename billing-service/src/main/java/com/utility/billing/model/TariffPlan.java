package com.utility.billing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor   
@NoArgsConstructor              

@Document(collection = "tariff_plans")
public class TariffPlan {

    @Id
    private String id;

    private UtilityType utilityType;   
    private String planCode;           

    private boolean active;
    public TariffPlan(UtilityType utilityType, String planCode, boolean active) {
        this.utilityType = utilityType;
        this.planCode = planCode;
        this.active = active;
    }
}