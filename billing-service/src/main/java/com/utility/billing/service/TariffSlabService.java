package com.utility.billing.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.utility.billing.dto.TariffSlabDto;
import com.utility.billing.exception.ApiException;
import com.utility.billing.model.TariffSlab;
import com.utility.billing.repository.TariffSlabRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TariffSlabService {

    private final TariffSlabRepository repository;

    public TariffSlabDto createSlab(TariffSlabDto dto) {

        TariffSlab slab = toEntity(dto);

        List<TariffSlab> existingSlabs =
                repository.findByUtilityTypeAndPlanCodeOrderByMinUnitsAsc(
                        slab.getUtilityType(),
                        slab.getPlanCode()
                );

        boolean overlap =
                existingSlabs.stream().anyMatch(existing ->
                        rangesOverlap(
                                existing.getMinUnits(),
                                existing.getMaxUnits(),
                                slab.getMinUnits(),
                                slab.getMaxUnits()
                        )
                );

        if (overlap) {
            throw new ApiException(
                    "Tariff slab range overlaps with existing slab",
                    HttpStatus.CONFLICT
            );
        }

        TariffSlab saved = repository.save(slab);
        return toDto(saved);
    }

    public TariffSlabDto deleteSlab(String id) {

        TariffSlab slab =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ApiException(
                                        "Tariff slab not found",
                                        HttpStatus.NOT_FOUND
                                )
                        );

        repository.delete(slab);
        return toDto(slab);
    }

    private TariffSlab toEntity(TariffSlabDto dto) {

        TariffSlab slab = new TariffSlab();
        slab.setMinUnits(dto.getMinUnits());
        slab.setMaxUnits(dto.getMaxUnits());
        slab.setRate(dto.getRate());
        return slab;
    }

    private TariffSlabDto toDto(TariffSlab slab) {

        return new TariffSlabDto(
                slab.getMinUnits(),
                slab.getMaxUnits(),
                slab.getRate()
        );
    }

    private boolean rangesOverlap(
            long min1,
            long max1,
            long min2,
            long max2
    ) {
        return min1 <= max2 && min2 <= max1;
    }
}