package com.utility.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TariffSlabDto {
    private long minUnits;
    private long maxUnits;
    private double rate;
}