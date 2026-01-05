package com.utility.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffSlabDto {
    private long minUnits;
    private long maxUnits;
    private double rate;
}