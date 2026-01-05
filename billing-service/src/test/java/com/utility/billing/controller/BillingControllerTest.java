package com.utility.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.billing.dto.BillResponse;
import com.utility.billing.dto.GenerateBillRequest;
import com.utility.billing.model.BillStatus;
import com.utility.billing.service.BillingService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillingService service;

    @Autowired
    private ObjectMapper objectMapper;

    private BillResponse mockBill() {
        BillResponse r = new BillResponse();
        r.setId("B1");
        r.setConsumerId("C1");
        r.setStatus(BillStatus.GENERATED);
        return r;
    }

    @Test
    void generate_success() throws Exception {

        GenerateBillRequest req = new GenerateBillRequest();
        req.setConsumerId("C1");

        Mockito.when(service.generateBill(Mockito.any()))
                .thenReturn(mockBill());

        mockMvc.perform(post("/bills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void byConsumer_success() throws Exception {

        Mockito.when(service.getBillsByConsumer("C1"))
                .thenReturn(List.of(mockBill()));

        mockMvc.perform(get("/bills/consumer/C1"))
                .andExpect(status().isOk());
    }

    @Test
    void markPaid_success() throws Exception {

        mockMvc.perform(put("/bills/B1/mark-paid"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getById_success() throws Exception {

        Mockito.when(service.getBillById("B1"))
                .thenReturn(mockBill());

        mockMvc.perform(get("/bills/B1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBills_withFilters() throws Exception {

        Mockito.when(service.getAllBills(
                BillStatus.GENERATED, 1, 2025, "C1"))
                .thenReturn(List.of(mockBill()));

        mockMvc.perform(get("/bills")
                .param("status", "GENERATED")
                .param("month", "1")
                .param("year", "2025")
                .param("consumerId", "C1"))
                .andExpect(status().isOk());
    }
}