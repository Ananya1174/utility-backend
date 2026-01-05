package com.utility.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.billing.dto.BillResponse;
import com.utility.billing.dto.GenerateBillRequest;
import com.utility.billing.model.BillStatus;
import com.utility.billing.service.BillingService;
import com.utility.billing.config.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = BillingController.class,
    excludeFilters = @Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillingService billingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generate_success() throws Exception {
        Mockito.when(billingService.generateBill(Mockito.any()))
                .thenReturn(new BillResponse());

        mockMvc.perform(post("/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GenerateBillRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void byConsumer_success() throws Exception {
        Mockito.when(billingService.getBillsByConsumer("C1"))
                .thenReturn(List.of(new BillResponse()));

        mockMvc.perform(get("/bills/consumer/C1"))
                .andExpect(status().isOk());
    }

    @Test
    void markPaid_success() throws Exception {
        Mockito.doNothing().when(billingService).markBillAsPaid("B1");

        mockMvc.perform(put("/bills/B1/mark-paid"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getById_success() throws Exception {
        Mockito.when(billingService.getBillById("B1"))
                .thenReturn(new BillResponse());

        mockMvc.perform(get("/bills/B1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBills_withFilters() throws Exception {
        Mockito.when(billingService.getAllBills(
                        BillStatus.PAID, 1, 2025, "C1"))
                .thenReturn(List.of(new BillResponse()));

        mockMvc.perform(get("/bills")
                        .param("status", "PAID")
                        .param("month", "1")
                        .param("year", "2025")
                        .param("consumerId", "C1"))
                .andExpect(status().isOk());
    }
}