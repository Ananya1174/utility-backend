package com.utility.billing.dto;

import com.utility.billing.model.TariffSlab;
import com.utility.billing.model.UtilityType;
import lombok.Data;

@Data
public class TariffSlabResponse {

    private String id;
    private UtilityType utilityType;
    private String planCode;
    private long minUnits;
    private long maxUnits;
    private double rate;

    public static TariffSlabResponse from(TariffSlab slab) {
        TariffSlabResponse r = new TariffSlabResponse();
        r.setId(slab.getId());
        r.setUtilityType(slab.getUtilityType());
        r.setPlanCode(slab.getPlanCode());
        r.setMinUnits(slab.getMinUnits());
        r.setMaxUnits(slab.getMaxUnits());
        r.setRate(slab.getRate());
        return r;
    }
}