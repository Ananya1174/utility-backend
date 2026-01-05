package com.utility.billing.model;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor   
@NoArgsConstructor  
@Document(collection = "tariff_slabs")
public class TariffSlab {

    @Id
    private String id;
    
    @Field(targetType = FieldType.STRING)  
    private UtilityType utilityType;
    private String planCode;        

    private long minUnits;
    private long maxUnits;
    private double rate;            
    public TariffSlab(UtilityType utilityType, String planCode,
            int minUnits, int maxUnits, double rate) {
this.utilityType = utilityType;
this.planCode = planCode;
this.minUnits = minUnits;
this.maxUnits = maxUnits;
this.rate = rate;
}
}