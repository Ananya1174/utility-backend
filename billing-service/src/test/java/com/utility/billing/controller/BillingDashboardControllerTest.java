package com.utility.billing.controller;

import com.utility.billing.dto.BillResponse;
import com.utility.billing.dto.dashboard.*;
import com.utility.billing.model.UtilityType;
import com.utility.billing.service.BillingDashboardService;
import com.utility.billing.service.BillingService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillingDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
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
        .thenReturn(new BillsSummaryDto(
                1,      // month
                2025,   // year
                10,     // totalBills
                6,      // paidBills
                4       // unpaidBills
        ));

        mockMvc.perform(get("/dashboard/billing/bills-summary")
                .param("month", "1")
                .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void consumptionSummary_success() throws Exception {

    	Mockito.when(dashboardService.getConsumptionSummary(1, 2025))
        .thenReturn(List.of(
                new ConsumptionSummaryDto(
                        UtilityType.ELECTRICITY,
                        1200L
                )
        ));

        mockMvc.perform(get("/dashboard/billing/consumption-summary")
                .param("month", "1")
                .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void averageConsumption_success() throws Exception {

    	Mockito.when(dashboardService.getAverageConsumption(1, 2025))
        .thenReturn(List.of(
                new AverageConsumptionDto(
                        UtilityType.ELECTRICITY,
                        400.5
                )
        ));

        mockMvc.perform(get("/dashboard/billing/consumption-average")
                .param("month", "1")
                .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void consumerBillingSummary_success() throws Exception {

    	Mockito.when(dashboardService.getConsumerBillingSummary(1, 2025))
        .thenReturn(List.of(
                new ConsumerBillingSummaryDto(
                        "C1",
                        5,          // totalBills
                        2500.0,     // totalAmount
                        1800.0,     // paidAmount
                        700.0       // unpaidAmount
                )
        ));

        mockMvc.perform(get("/dashboard/billing/consumer-summary")
                .param("month", "1")
                .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void totalBilledMonthly_success() throws Exception {

        Mockito.when(dashboardService.getTotalBilledForMonth(1, 2025))
                .thenReturn(1000.0);

        mockMvc.perform(get("/dashboard/billing/total-billed-monthly")
                .param("month", "1")
                .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void consumerBillingHistory_success() throws Exception {

        Mockito.when(dashboardService.getConsumerBillingHistory("C1"))
                .thenReturn(List.of(new BillResponse()));

        mockMvc.perform(get("/dashboard/billing/consumer/C1"))
                .andExpect(status().isOk());
    }

    @Test
    void totalBilled_success() throws Exception {

        Mockito.when(billingService.getTotalBilledAmount())
                .thenReturn(5000.0);

        mockMvc.perform(get("/dashboard/billing/total-billed"))
                .andExpect(status().isOk());
    }
}