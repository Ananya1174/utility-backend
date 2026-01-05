package com.utility.billing.dto.dashboard;
import lombok.Data;
import lombok.AllArgsConstructor;
import com.utility.billing.model.UtilityType;

@Data
@AllArgsConstructor
public class ConsumptionSummaryDto {
    private UtilityType utilityType;
    private long totalUnits;
}