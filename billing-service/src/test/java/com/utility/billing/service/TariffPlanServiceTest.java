package com.utility.billing.service;

import com.utility.billing.dto.TariffPlanDto;
import com.utility.billing.exception.ApiException;
import com.utility.billing.model.TariffPlan;
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

        TariffPlanDto dto =
                new TariffPlanDto("DOMESTIC", List.of());

        TariffPlan savedPlan = new TariffPlan();
        savedPlan.setPlanCode("DOMESTIC");
        savedPlan.setActive(true);

        when(repository.existsByUtilityTypeAndPlanCode(any(), eq("DOMESTIC")))
                .thenReturn(false);

        when(repository.save(any(TariffPlan.class)))
                .thenReturn(savedPlan);

        TariffPlanDto result = service.createTariffPlan(dto);

        assertEquals("DOMESTIC", result.getPlanCode());
    }

    @Test
    void deactivateTariffPlan_success() {

        TariffPlan plan = new TariffPlan();
        plan.setActive(true);
        plan.setPlanCode("DOMESTIC");

        when(repository.findById("T1"))
                .thenReturn(Optional.of(plan));

        var response = service.deactivateTariffPlan("T1");

        assertTrue(response.get("message").contains("deactivated"));
        verify(repository).save(plan);
    }

    @Test
    void deactivateTariffPlan_alreadyInactive() {

        TariffPlan plan = new TariffPlan();
        plan.setActive(false);

        when(repository.findById("T1"))
                .thenReturn(Optional.of(plan));

        var response = service.deactivateTariffPlan("T1");

        assertTrue(response.get("message").contains("inactive"));
        verify(repository, never()).save(any());
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

        when(repository.findAll())
                .thenReturn(List.of(plan));

        assertEquals(1, service.getPlans(false).size());
    }

    @Test
    void deactivateTariffPlan_notFound() {

        when(repository.findById("T1"))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.deactivateTariffPlan("T1"));
    }
}