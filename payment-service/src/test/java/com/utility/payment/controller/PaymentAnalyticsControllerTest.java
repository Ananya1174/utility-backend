package com.utility.payment.controller;

import com.utility.payment.dto.dashboard.*;
import com.utility.payment.model.PaymentMode;
import com.utility.payment.service.PaymentAnalyticsService;
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

@WebMvcTest(PaymentAnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentAnalyticsService analyticsService;

    @Test
    void revenueSummary_success() throws Exception {
        Mockito.when(analyticsService.getMonthlyRevenue(1, 2025))
                .thenReturn(new RevenueSummaryDto(1, 2025, 1200.0, 4));

        mockMvc.perform(get("/dashboard/payments/revenue-summary")
                        .param("month", "1")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void outstandingSummary_success() throws Exception {
        Mockito.when(analyticsService.getOutstandingSummary())
                .thenReturn(new OutstandingSummaryDto(5000.0, 3500.0, 1500.0));

        mockMvc.perform(get("/dashboard/payments/outstanding-summary"))
                .andExpect(status().isOk());
    }

    @Test
    void paymentsSummary_success() throws Exception {
        Mockito.when(analyticsService.getPaymentsSummary(1, 2025))
                .thenReturn(new PaymentsSummaryDto(1, 2025, 10, 2));

        mockMvc.perform(get("/dashboard/payments/payments-summary")
                        .param("month", "1")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void failedPayments_success() throws Exception {
        Mockito.when(analyticsService.getFailedPaymentsSummary(1, 2025))
                .thenReturn(new FailedPaymentSummaryDto(3, 450.0));

        mockMvc.perform(get("/dashboard/payments/failed-summary")
                        .param("month", "1")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void monthlyOutstanding_success() throws Exception {
        Mockito.when(analyticsService.getMonthlyOutstanding(2025))
                .thenReturn(List.of(
                        new MonthlyOutstandingDto(
                                1,
                                "JANUARY",
                                2000.0,
                                1500.0,
                                500.0
                        )
                ));

        mockMvc.perform(get("/dashboard/payments/outstanding-monthly")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void revenueByMode_success() throws Exception {
        Mockito.when(analyticsService.getRevenueByMode(1, 2025))
                .thenReturn(List.of(
                        new PaymentModeSummaryDto(PaymentMode.ONLINE, 3000.0)
                ));

        mockMvc.perform(get("/dashboard/payments/revenue-by-mode")
                        .param("month", "1")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void consumerPaymentSummary_success() throws Exception {
        Mockito.when(analyticsService.getConsumerPaymentSummary(1, 2025))
                .thenReturn(List.of(
                        new ConsumerPaymentSummaryDto("CONSUMER1", 5, 2500.0)
                ));

        mockMvc.perform(get("/dashboard/payments/consumer-summary")
                        .param("month", "1")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void yearlyRevenue_success() throws Exception {
        Mockito.when(analyticsService.getYearlyRevenue(2025))
                .thenReturn(List.of(
                        new RevenueSummaryDto(1, 2025, 1200.0, 4)
                ));

        mockMvc.perform(get("/dashboard/payments/revenue-yearly")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }
}
