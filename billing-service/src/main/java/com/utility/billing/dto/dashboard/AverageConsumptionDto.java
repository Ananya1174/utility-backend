package com.utility.billing.dto.dashboard;

import com.utility.billing.model.UtilityType;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AverageConsumptionDto {
    private UtilityType utilityType;
    private double averageUnits;
}