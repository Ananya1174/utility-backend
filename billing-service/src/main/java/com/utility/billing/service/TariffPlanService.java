package com.utility.billing.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.utility.billing.model.TariffPlan;
import com.utility.billing.repository.TariffPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TariffPlanService {

    private final TariffPlanRepository repository;

    /* ================= CREATE ================= */

    public TariffPlan createTariffPlan(TariffPlan plan) {

        if (repository.existsByUtilityTypeAndPlanCode(
                plan.getUtilityType(),
                plan.getPlanCode()
        )) {
            throw new RuntimeException("Tariff plan already exists");
        }

        plan.setActive(true);
        return repository.save(plan);
    }

    /* ================= DEACTIVATE ================= */

    public Map<String, String> deactivateTariffPlan(String id) {

        TariffPlan plan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tariff plan not found"));

        if (!plan.isActive()) {
            return Map.of("message", "Tariff plan already inactive");
        }

        plan.setActive(false);
        repository.save(plan);

        return Map.of(
                "message",
                "Tariff plan " + plan.getPlanCode() + " deactivated successfully"
        );
    }

    /* ================= QUERY ================= */

    public List<TariffPlan> getActivePlans() {
        return repository.findByActiveTrue();
    }

    /**
     * Admin view:
     *  - active = null → ALL
     *  - active = true → only active
     *  - active = false → only inactive
     */
    public List<TariffPlan> getPlans(Boolean active) {

        if (active == null) {
            return repository.findAll();
        }

        if (active) {
            return repository.findByActiveTrue();
        }

        // inactive
        return repository.findAll()
                .stream()
                .filter(plan -> !plan.isActive())
                .toList();
    }
}