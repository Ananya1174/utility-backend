package com.utility.billing.repository;

import com.utility.billing.model.TariffPlan;
import com.utility.billing.model.UtilityType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TariffPlanRepository
        extends MongoRepository<TariffPlan, String> {

    boolean existsByUtilityTypeAndPlanCode(
            UtilityType utilityType,
            String planCode
    );

    Optional<TariffPlan> findByUtilityTypeAndPlanCodeAndActiveTrue(
            UtilityType utilityType,
            String planCode
    );

    List<TariffPlan> findByActiveTrue();
    List<TariffPlan> findByUtilityTypeAndActiveTrue(
            UtilityType utilityType
    );
    List<TariffPlan> findAll();
}