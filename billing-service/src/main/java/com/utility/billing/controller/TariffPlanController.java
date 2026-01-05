package com.utility.billing.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.utility.billing.model.TariffPlan;
import com.utility.billing.service.TariffPlanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tariffs/plans")
@RequiredArgsConstructor
public class TariffPlanController {

    private final TariffPlanService tariffPlanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TariffPlan create(@RequestBody TariffPlan plan) {
        return tariffPlanService.createTariffPlan(plan);
    }

    @PutMapping("/{id}/deactivate")
    public Map<String, String> deactivate(
            @PathVariable("id") String id) {
        return tariffPlanService.deactivateTariffPlan(id);
    }

    @GetMapping
    public List<TariffPlan> getPlans(
    		  @RequestParam(name = "active", required = false) Boolean active) {
        return tariffPlanService.getPlans(active);
    }

    @GetMapping("/active")
    public List<TariffPlan> activePlans() {
        return tariffPlanService.getActivePlans();
    }
}