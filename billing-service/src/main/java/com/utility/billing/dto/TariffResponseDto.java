package com.utility.billing.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffResponseDto {
    private String utilityType;
    private List<TariffPlanDto> plans;
}