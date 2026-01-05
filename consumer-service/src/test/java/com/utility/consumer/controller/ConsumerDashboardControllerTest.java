package com.utility.consumer.controller;

import com.utility.consumer.dto.dashboard.ConsumerDashboardSummaryDto;
import com.utility.consumer.service.ConsumerDashboardService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

@WebMvcTest(ConsumerDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConsumerDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumerDashboardService dashboardService;

    @Test
    void summary_success() throws Exception {

        // ✅ MANUALLY SET AUTHENTICATION
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken("C1", null)
        );
        SecurityContextHolder.setContext(context);

        Mockito.when(dashboardService.getDashboardSummary("C1"))
        .thenReturn(new ConsumerDashboardSummaryDto(
                1,              // activeUtilities
                0,              // pendingRequests
                2,              // totalBills
                1,              // unpaidBills
                500.0,          // totalOutstanding
                500.0,          // lastPaymentAmount
                LocalDateTime.now()
        ));

        mockMvc.perform(get("/consumers/dashboard/summary"))
                .andExpect(status().isOk());
    }
}