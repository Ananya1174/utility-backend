package com.utility.billing.repository;

import com.utility.billing.model.TariffSlab;
import com.utility.billing.model.UtilityType;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TariffSlabRepository
        extends MongoRepository<TariffSlab, String> {

    List<TariffSlab> findByUtilityTypeAndPlanCodeOrderByMinUnitsAsc(
    		UtilityType utilityType,
            String planCode
    );
}