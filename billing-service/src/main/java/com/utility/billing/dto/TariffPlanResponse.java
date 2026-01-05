package com.utility.billing.dto;

import com.utility.billing.model.TariffPlan;
import com.utility.billing.model.UtilityType;
import lombok.Data;

@Data
public class TariffPlanResponse {

    private String id;
    private UtilityType utilityType;
    private String planCode;
    private boolean active;

    public static TariffPlanResponse from(TariffPlan plan) {
        TariffPlanResponse r = new TariffPlanResponse();
        r.setId(plan.getId());
        r.setUtilityType(plan.getUtilityType());
        r.setPlanCode(plan.getPlanCode());
        r.setActive(plan.isActive());
        return r;
    }
}