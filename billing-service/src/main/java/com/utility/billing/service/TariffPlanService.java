package com.utility.billing.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.utility.billing.dto.TariffPlanDto;
import com.utility.billing.exception.ApiException;
import com.utility.billing.model.TariffPlan;
import com.utility.billing.repository.TariffPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TariffPlanService {

    private final TariffPlanRepository repository;

    public TariffPlanDto createTariffPlan(TariffPlanDto dto) {

        TariffPlan plan = toEntity(dto);

        boolean exists =
                repository.existsByUtilityTypeAndPlanCode(
                        plan.getUtilityType(),
                        plan.getPlanCode()
                );

        if (exists) {
            throw new ApiException(
                    "Tariff plan already exists",
                    HttpStatus.CONFLICT
            );
        }

        plan.setActive(true);
        TariffPlan saved = repository.save(plan);
        return toDto(saved);
    }

    public Map<String, String> deactivateTariffPlan(String id) {

        TariffPlan plan =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ApiException(
                                        "Tariff plan not found",
                                        HttpStatus.NOT_FOUND
                                )
                        );

        if (!plan.isActive()) {
            return Map.of(
                    "message",
                    "Tariff plan already inactive"
            );
        }

        plan.setActive(false);
        repository.save(plan);

        return Map.of(
                "message",
                "Tariff plan "
                        + plan.getPlanCode()
                        + " deactivated successfully"
        );
    }

    public List<TariffPlanDto> getActivePlans() {

        return repository.findByActiveTrue()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<TariffPlanDto> getPlans(Boolean active) {

        List<TariffPlan> plans;

        if (active == null) {
            plans = repository.findAll();
        } else if (active) {
            plans = repository.findByActiveTrue();
        } else {
            plans = repository.findAll()
                    .stream()
                    .filter(plan -> !plan.isActive())
                    .toList();
        }

        return plans.stream()
                .map(this::toDto)
                .toList();
    }

    private TariffPlan toEntity(TariffPlanDto dto) {

        TariffPlan plan = new TariffPlan();
        plan.setPlanCode(dto.getPlanCode());
        return plan;
    }

    private TariffPlanDto toDto(TariffPlan plan) {

        return new TariffPlanDto(
                plan.getPlanCode(),
                List.of()
        );
    }
}