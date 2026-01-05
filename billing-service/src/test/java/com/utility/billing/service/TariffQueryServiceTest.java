package com.utility.billing.service;

import com.utility.billing.dto.TariffResponseDto;
import com.utility.billing.model.*;
import com.utility.billing.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffQueryServiceTest {

    @Mock
    private TariffPlanRepository planRepo;

    @Mock
    private TariffSlabRepository slabRepo;

    @InjectMocks
    private TariffQueryService service;

    @Test
    void getTariffsByUtility_success() {

        TariffPlan plan = new TariffPlan();
        plan.setPlanCode("DOM");

        when(planRepo.findByUtilityTypeAndActiveTrue(UtilityType.ELECTRICITY))
                .thenReturn(List.of(plan));

        TariffSlab slab = new TariffSlab();
        slab.setMinUnits(0);
        slab.setMaxUnits(100);
        slab.setRate(5);

        when(slabRepo.findByUtilityTypeAndPlanCodeOrderByMinUnitsAsc(
                UtilityType.ELECTRICITY, "DOM"))
                .thenReturn(List.of(slab));

        TariffResponseDto dto =
                service.getTariffsByUtility(UtilityType.ELECTRICITY);

        assertEquals(1, dto.getPlans().size());
    }
}