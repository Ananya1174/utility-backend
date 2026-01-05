package com.utility.billing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.utility.billing.dto.TariffPlanDto;
import com.utility.billing.dto.TariffResponseDto;
import com.utility.billing.dto.TariffSlabDto;
import com.utility.billing.model.TariffPlan;
import com.utility.billing.model.TariffSlab;
import com.utility.billing.model.UtilityType;
import com.utility.billing.repository.TariffPlanRepository;
import com.utility.billing.repository.TariffSlabRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TariffQueryService {

	private final TariffPlanRepository planRepo;
	private final TariffSlabRepository slabRepo;

	public TariffResponseDto getTariffsByUtility(UtilityType utilityType) {

		List<TariffPlan> plans = planRepo.findByUtilityTypeAndActiveTrue(utilityType);

		if (plans.isEmpty()) {
			return new TariffResponseDto(utilityType.name(), List.of());
		}

		List<TariffPlanDto> planDtos = plans.stream().map(plan -> {

			List<TariffSlab> slabs = slabRepo.findByUtilityTypeAndPlanCodeOrderByMinUnitsAsc(utilityType,
					plan.getPlanCode());

			List<TariffSlabDto> slabDtos = slabs.stream()
					.map(slab -> new TariffSlabDto(slab.getMinUnits(), slab.getMaxUnits(), slab.getRate()))
					.toList();

			return new TariffPlanDto(plan.getPlanCode(), slabDtos);
		}).toList();

		return new TariffResponseDto(utilityType.name(), planDtos);
	}
}