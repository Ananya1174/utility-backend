package com.utility.billing.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class TariffPlanDto {
    private String planCode;
    private List<TariffSlabDto> slabs;
}