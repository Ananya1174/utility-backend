package com.utility.billing.service;

import com.utility.billing.exception.ApiException;
import com.utility.billing.model.TariffSlab;
import com.utility.billing.repository.TariffSlabRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TariffSlabService {

    private final TariffSlabRepository repository;

    public TariffSlab deleteSlab(String slabId) {

        TariffSlab slab = repository.findById(slabId)
                .orElseThrow(() ->
                        new ApiException("Tariff slab not found", HttpStatus.NOT_FOUND)
                );

        repository.delete(slab);
        return slab;
    }
}