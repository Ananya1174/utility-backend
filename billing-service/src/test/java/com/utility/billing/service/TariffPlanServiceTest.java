package com.utility.billing.service;

import com.utility.billing.model.TariffPlan;
import com.utility.billing.model.UtilityType;
import com.utility.billing.repository.TariffPlanRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffPlanServiceTest {

    @Mock
    private TariffPlanRepository repository;

    @InjectMocks
    private TariffPlanService service;

    @Test
    void createTariffPlan_success() {

        TariffPlan plan = new TariffPlan();
        plan.setUtilityType(UtilityType.ELECTRICITY);
        plan.setPlanCode("DOM");

        when(repository.existsByUtilityTypeAndPlanCode(
                UtilityType.ELECTRICITY, "DOM"))
                .thenReturn(false);

        when(repository.save(any())).thenReturn(plan);

        assertTrue(service.createTariffPlan(plan).isActive());
    }

    @Test
    void deactivateTariffPlan_success() {

        TariffPlan plan = new TariffPlan();
        plan.setActive(true);
        plan.setPlanCode("DOM");

        when(repository.findById("T1"))
                .thenReturn(Optional.of(plan));

        assertTrue(service.deactivateTariffPlan("T1")
                .get("message")
                .contains("deactivated"));
    }
    @Test
    void deactivateTariffPlan_lambdaCovered() {

        TariffPlan plan = new TariffPlan();
        plan.setActive(true);

        when(repository.findById("T1"))
                .thenReturn(Optional.of(plan));

        service.deactivateTariffPlan("T1");

        verify(repository).save(plan);   // ⭐ THIS triggers lambda coverage
    }
    @Test
    void getPlans_activeTrue() {
        when(repository.findByActiveTrue())
                .thenReturn(List.of(new TariffPlan()));

        assertEquals(1, service.getPlans(true).size());
    }
    

    @Test
    void getPlans_null() {
        when(repository.findAll())
                .thenReturn(List.of(new TariffPlan()));

        assertEquals(1, service.getPlans(null).size());
    }

    @Test
    void getPlans_inactive() {

        TariffPlan plan = new TariffPlan();
        plan.setActive(false);

        when(repository.findAll()).thenReturn(List.of(plan));

        assertEquals(1, service.getPlans(false).size());
    }
}