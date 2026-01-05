package com.utility.billing.controller;

import com.utility.billing.config.SecurityConfig;
import com.utility.billing.service.BillingDashboardService;
import com.utility.billing.service.BillingService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = BillingDashboardController.class,
    excludeFilters = @Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BillingDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillingDashboardService dashboardService;

    @MockBean
    private BillingService billingService;

    @Test
    void billsSummary_success() throws Exception {
        Mockito.when(dashboardService.getBillsSummary(1, 2025))
                .thenReturn(null);

        mockMvc.perform(get("/dashboard/billing/bills-summary")
                        .param("month", "1")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void consumptionSummary_success() throws Exception {
        Mockito.when(dashboardService.getConsumptionSummary(1, 2025))
                .thenReturn(List.of());

        mockMvc.perform(get("/dashboard/billing/consumption-summary")
                        .param("month", "1")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void totalBilled_success() throws Exception {
        Mockito.when(billingService.getTotalBilledAmount())
                .thenReturn(1000.0);

        mockMvc.perform(get("/dashboard/billing/total-billed"))
                .andExpect(status().isOk());
    }
}